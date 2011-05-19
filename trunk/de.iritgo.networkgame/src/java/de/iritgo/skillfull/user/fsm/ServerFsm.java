package de.iritgo.skillfull.user.fsm;

import com.artemis.Entity;
import com.artemis.utils.Utils;

import de.iritgo.skillfull.component.common.Expires;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.SegmentMove;
import de.iritgo.skillfull.component.motion.SegmentMoveSystem;
import de.iritgo.skillfull.entity.EntityManager;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.entity.network.UserEntityCreateMessage;
import de.iritgo.skillfull.fsm.Action;
import de.iritgo.skillfull.fsm.Fsm;
import de.iritgo.skillfull.fsm.FsmBuilder;
import de.iritgo.skillfull.fsm.FsmEventBus;
import de.iritgo.skillfull.fsm.SimpleActionProcessor;
import de.iritgo.skillfull.fsm.State;
import de.iritgo.skillfull.user.FsmEventTypes;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.UserFsmBusEventHandler;
import de.iritgo.skillfull.user.UserManager;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.user.network.LoginResponseMessage;
import de.iritgo.skillfull.user.network.NewUserMessage;
import de.iritgo.skillfull.user.network.UserMessage;
import de.iritgo.skillfull.world.CommonWorld;

public class ServerFsm
{

	private Fsm fsm;

	public void init (final CommonWorld commonWorld, final UserManager userManager)
	{
		fsm = new Fsm ();
		FsmBuilder b = new FsmBuilder (FsmEventTypes.values ().length, new SimpleActionProcessor ());

		b.state (FsmEventTypes.IDLE.id ()).name ("Idle").addTrans (new LoginRequestMessage ());

		b.state (FsmEventTypes.LOGIN.id ()).name ("Login").addTrans (new IdleMessage ());
		b.state (FsmEventTypes.LOGIN.id ()).name ("Login").addTrans (new LoginRequestMessage ());
		b.state (FsmEventTypes.LOGIN.id ()).name ("Login").addAction (
						State.ON_EVENT,
						new LoginRequestMessage (),
						new Action<LoginRequestMessage> ()
						{
							@Override
							public boolean execute (LoginRequestMessage event)
							{
								User user = userManager.registerUser (event.getName (), event.getPassword ());
								user.setNetworkId (event.getNetworkId ());
								user.setClientId (event.getClientId ());
								commonWorld.getEventBusManager ().publish (new IdleMessage ());
								commonWorld.getNetwork ().sendMessage (user, new LoginResponseMessage ((byte) user.getId (), (byte) 1));

								commonWorld.getBotTest ().sendBots (user);
								
								EntityManager entityManager = commonWorld.getSkillFullEntityManager ();
								Entity playerEntity = entityManager.createNewUserEntity (user, -1);
								playerEntity.getComponent (PositionHistory.class).setExpandableTimeRange (true);
								playerEntity.getComponent (Position.class).setLocation (100, 100);
								user.setEntityId (playerEntity.getUniqueId ());
								UserEntityCreateMessage userEntityCreateMessage = new UserEntityCreateMessage ((byte)1);
								userEntityCreateMessage.setUniqueEntityId ((int) playerEntity.getUniqueId ());
								commonWorld.getNetwork ().sendMessage (user, userEntityCreateMessage);

								for (User otherUser : userManager.getUsers ())
								{
									if (otherUser == user)
										continue;
									
									EntityCreateMessage otherUserEntity = new EntityCreateMessage ((byte)1);
									otherUserEntity.setUniqueEntityId ((int) otherUser.getEntityId ());
									
									NewUserMessage newUserMessage = new NewUserMessage ((byte)otherUser.getId (), otherUser.getName ());
									
									commonWorld.getNetwork ().sendMessage (user, otherUserEntity);
									commonWorld.getNetwork ().sendMessage (user, newUserMessage);
								}

								for (User otherUser : userManager.getUsers ())
								{
									if (otherUser == user)
										continue;

									EntityCreateMessage otherUserEntity = new EntityCreateMessage ((byte)1);
									otherUserEntity.setUniqueEntityId ((int) user.getEntityId ());
									NewUserMessage newUserMessage = new NewUserMessage ((byte)user.getId (), user.getName ());
									commonWorld.getNetwork ().sendMessage (otherUser, otherUserEntity);
									commonWorld.getNetwork ().sendMessage (otherUser, newUserMessage);
								}


								for (int q = 1 ; q < 0 ; ++q)
								{
									Entity entity = entityManager.createByTemplateId ((byte) 2);
									entity.getComponent (Position.class).setLocation (500, 500);
									entity.refresh ();
									EntityCreateMessage entityCreateMessage2 = new EntityCreateMessage ((byte) 2);
									entityCreateMessage2.setUniqueEntityId ((int) entity.getUniqueId ());
									entityCreateMessage2.setTick ((int)commonWorld.getGameTimeManager ().getNetworkTime ());

									EntitySegmentMoveMessage entitySegmentMoveMessage = new EntitySegmentMoveMessage ();
									entitySegmentMoveMessage.setTick ((int)commonWorld.getGameTimeManager ().getNetworkTime ());
									entitySegmentMoveMessage.setStartX ((int) 0);
									entitySegmentMoveMessage.setStartY ((int) 0);
									entitySegmentMoveMessage.setEndX ((int) 500);
									entitySegmentMoveMessage.setEndY ((int) 500);
									// 3 Pixel / Sek. v = weg/zeit
									// v = Pixel/t
									// v = 1 Pixel/1000ms
									float dist = Utils.distance (entitySegmentMoveMessage.getStartX (), entitySegmentMoveMessage.getStartY (),
													entitySegmentMoveMessage.getEndX (), entitySegmentMoveMessage.getEndY ());
									float t = (dist) / (float) (0.01 * 15);
									entitySegmentMoveMessage.setDestinationTick ((int)commonWorld.getGameTimeManager ().getNetworkTime () + (int) t);
									entitySegmentMoveMessage.setUniqueEntityId ((int) entity.getUniqueId ());

									Expires entityExpires = entity.getComponent (Expires.class);
									entityExpires.setLifeTime ((int) t + 1000);
									entityCreateMessage2.setLifeTime ((int) t);

									SegmentMove moveWayComp = entity.getComponent (SegmentMove.class);
									moveWayComp.setStartPoint (new Position (entitySegmentMoveMessage.getStartX (), entitySegmentMoveMessage.getStartY ()));
									moveWayComp.setEndPoint (new Position (entitySegmentMoveMessage.getEndX (), entitySegmentMoveMessage.getEndY ()));
									moveWayComp.setStartTick (entitySegmentMoveMessage.getTick ());
									moveWayComp.setDestinationTick (entitySegmentMoveMessage.getDestinationTick ());

									for (User dstUser : userManager.getUsers ())
									{
										commonWorld.getNetwork ().sendMessage (dstUser, entityCreateMessage2);
										commonWorld.getNetwork ().sendMessage (dstUser, entitySegmentMoveMessage);
									}
								}

								return true;
							}
						});

		fsm.addStates (b.getStates (), FsmEventTypes.IDLE.id ());

		UserFsmBusEventHandler handler = new UserFsmBusEventHandler (fsm);
		FsmEventBus fsmEventBus = new FsmEventBus ();

		fsmEventBus.subscribe (UserMessage.class, handler);

		commonWorld.getEventBusManager ().addEventBus (fsmEventBus);
	}
}
