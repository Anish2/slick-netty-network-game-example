/*
 * Copyright (c) 2008-2010, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.iritgo.networkgame;


import de.matthiasmann.twl.DesktopArea;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;


//import test.SimpleTest;

/**
 * A chat demo
 *
 * This class also acts as root pane
 *
 * @author Matthias Mann
 */

public class ChatFrame extends ResizableFrame
{
	private static ChatFrame chatFrame;

	private final StringBuilder sb;

	private final HTMLTextAreaModel textAreaModel;

	private final TextArea textArea;

	private final EditField editField;

	private final ScrollPane scrollPane;

	private int curColor;

	private NetworkGame fumag;

	public ChatFrame (final NetworkGame fumag)
	{
		setTitle ("Chat");
		this.fumag = fumag;
		
		this.sb = new StringBuilder ();
		this.textAreaModel = new HTMLTextAreaModel ();
		this.textArea = new TextArea (textAreaModel);
		this.editField = new EditField ();

		editField.addCallback (new EditField.Callback ()
		{
			public void callback (int key)
			{
				if (key == Event.KEY_RETURN)
				{
					// cycle through 3 different colors/font styles
					fumag.localChatText (editField.getText ());
					appendRow ("color" + curColor, editField.getText ());

					editField.setText ("");
					curColor = (curColor + 1) % 3;
				}
			}
		});

		textArea.addCallback (new TextArea.Callback ()
		{
			public void handleLinkClicked (String href)
			{
				Sys.openURL (href);
			}
		});

		scrollPane = new ScrollPane (textArea);
		scrollPane.setFixed (ScrollPane.Fixed.HORIZONTAL);

		DialogLayout l = new DialogLayout ();
		l.setTheme ("content");
		l.setHorizontalGroup (l.createParallelGroup (scrollPane, editField));
		l.setVerticalGroup (l.createSequentialGroup (scrollPane, editField));

		add (l);

		appendRow ("default", "Welcome to a little tech demo... :)");
		appendRow ("default", "it was made for the slick contest 2011");
		appendRow ("default", ".");
		appendRow ("default", "I hope the netcode runs fine!");
		appendRow ("default", ".");
		appendRow ("default", "Short instuctions:");
		appendRow ("default", "Create a server type: server <host-ip> <host-port>[enter]");
		appendRow ("default", "or you can create a localhost server with 'server' and enter");
		appendRow ("default", ".");
		appendRow ("default", "You can connect to a server with this command:");
		appendRow ("default", "client <username> <local-bind-ip> <local-bind-port> <server-ip> <server-port>[enter]");
		appendRow ("default", "or you write 'client' 'username' and you will connect the localhost server.");
		appendRow ("default", "for an second client on the same pc you must use the first login method.");
		appendRow ("default", ".");
		appendRow ("default", "You play with adws -> left, right, up, down and");
		appendRow ("default", "you can fire apples with the left mouse button.");
		appendRow ("default", ". ");
		appendRow ("default", "if you press 'tab' you can disable the chat window");
		appendRow ("default", ".");
		appendRow ("default", ".");
		appendRow ("default", "I hope you enjoy my network game :-)");
		appendRow ("default", ".");
		appendRow ("default", "Thanks for all the help from the slick community!");
		appendRow ("default", ".");
		appendRow ("default", "Note: Target the head..add bots with 'bots' <number>");
	}

	public void appendRow (String font, String text)
	{
		sb.append ("<div style=\"word-wrap: break-word; font-family: ").append (font).append ("; \">");
		// not efficient but simple
		for (int i = 0, l = text.length (); i < l; i++)
		{
			char ch = text.charAt (i);
			switch (ch)
			{
				case '<':
					sb.append ("&lt;");
					break;
				case '>':
					sb.append ("&gt;");
					break;
				case '&':
					sb.append ("&amp;");
					break;
				case '"':
					sb.append ("&quot;");
					break;
				case ':':
					if (text.startsWith (":)", i))
					{
						sb.append ("<img src=\"smiley\" alt=\":)\"/>");
						i += 1; // skip one less because of i++ in the for loop
						break;
					}
					sb.append (ch);
					break;
				case 'h':
					if (text.startsWith ("http://", i))
					{
						int end = i + 7;
						while (end < l && isURLChar (text.charAt (end)))
						{
							end++;
						}
						String href = text.substring (i, end);
						sb.append ("<a style=\"font: link\" href=\"").append (href).append ("\" >").append (href)
										.append ("</a>");
						i = end - 1; // skip one less because of i++ in the for
										// loop
						break;
					}
					// fall through:
				default:
					sb.append (ch);
			}
		}
		sb.append ("</div>");

		boolean isAtEnd = scrollPane.getMaxScrollPosY () == scrollPane.getScrollPositionY ();

		textAreaModel.setHtml (sb.toString ());

		if (isAtEnd)
		{
			scrollPane.validateLayout ();
			scrollPane.setScrollPositionY (scrollPane.getMaxScrollPosY ());
		}
	}

	private boolean isURLChar (char ch)
	{
		return (ch == '.') || (ch == '/') || (ch == '%') || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')
						|| (ch >= 'A' && ch <= 'Z');
	}

	public static ChatFrame getInstance (NetworkGame fumag)
	{
		if (chatFrame == null)
		{
			chatFrame = new ChatFrame (fumag);
		}
		return chatFrame;
	}
}
