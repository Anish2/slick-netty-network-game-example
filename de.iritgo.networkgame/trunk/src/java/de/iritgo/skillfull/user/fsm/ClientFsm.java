package de.iritgo.skillfull.user.fsm;

import de.iritgo.skillfull.fsm.Action;
import de.iritgo.skillfull.fsm.Fsm;
import de.iritgo.skillfull.fsm.FsmBuilder;
import de.iritgo.skillfull.fsm.FsmEventBus;
import de.iritgo.skillfull.fsm.SimpleActionProcessor;
import de.iritgo.skillfull.fsm.State;
import de.iritgo.skillfull.user.FsmEventTypes;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.UserFsmBusEventHandler;
import de.iritgo.skillfull.user.network.LoginResponseMessage;
import de.iritgo.skillfull.user.network.NewUserMessage;
import de.iritgo.skillfull.user.network.UserMessage;
import de.iritgo.skillfull.world.CommonWorld;
import de.iritgo.skillfull.world.GameWorld;

public class ClientFsm
{

	private Fsm fsm;

	public void init (final CommonWorld commonWorld)
	{
		fsm = new Fsm ();
		FsmBuilder b = new FsmBuilder (FsmEventTypes.values ().length, new SimpleActionProcessor ());

		b.state (FsmEventTypes.IDLE.id ()).name ("TransTo->LoginResponse").addTrans (new LoginResponseMessage ());
		b.state (FsmEventTypes.IDLE.id ()).name ("TransTo->NewUser").addTrans (new NewUserMessage ());

		b.state (FsmEventTypes.LOGIN_RESPONSE.id ()).name ("NewUserMessage").addTrans (new NewUserMessage ());
		b.state (FsmEventTypes.LOGIN_RESPONSE.id ()).name ("LoginResponse").addTrans (new LoginResponseMessage ());
		b.state (FsmEventTypes.LOGIN_RESPONSE.id ()).name ("LoginResponse").addAction (
						State.ON_EVENT,
						new LoginResponseMessage (),
						new Action<LoginResponseMessage> ()
						{
							@Override
							public boolean execute (LoginResponseMessage event)
							{
								User user = commonWorld.getUserManager ().getAppUser ();
								commonWorld.getUserManager ().removeUser (user);
								user.setId (event.getUserId ());
								commonWorld.getUserManager ().addUser (user);
								commonWorld.getUserManager ().setAppUser (user);
								System.out.println ("Client is: " + (event.getLoginState () == 1 ? "Login" : "Error"));
								return true;
							}
						});

		b.state (FsmEventTypes.NEW_USER.id ()).name ("TransTo->NewUser").addTrans (new NewUserMessage ());
		b.state (FsmEventTypes.NEW_USER.id ()).name ("TransTo->Idle").addTrans (new IdleMessage ());
		b.state (FsmEventTypes.NEW_USER.id ()).name ("NewUserMessage").addAction (
						State.ON_EVENT,
						new NewUserMessage (),
						new Action<NewUserMessage> ()
						{
							@Override
							public boolean execute (NewUserMessage event)
							{
								User user = new User ();
								user.setName (event.getName ());
								user.setId (event.getUserId ());
								
								commonWorld.getUserManager ().addUser (user);
								commonWorld.getEventBusManager ().publish (new IdleMessage ());
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
