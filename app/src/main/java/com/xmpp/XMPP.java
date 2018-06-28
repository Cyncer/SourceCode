package com.xmpp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.ChatActivity;
import com.app.android.cync.R;
import com.utils.CommonClass;
import com.utils.Constants;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.commands.AdHocCommandManager;
import org.jivesoftware.smackx.commands.RemoteCommand;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
//import com.xmpp_android.adhoc.Custom_Command;
//import com.xmpp_android.adhoc.Custom_Command_Send;

public class XMPP {

    public static final int NOTIFICATION_ID = 1;
    private final static String TAG = "ServiceXMPP";

    XMPPConnectionListener connectionListener;
    PacketListener packetListener=null;
    // Login Parameters
    private String serverAddress;
    private String loginUser;
    private String passwordUser;

    private static XMPPConnection connection;

    ChatManager chatmanager;

    private String serverDomain;

    private boolean isConnected = false;

    // pubsub Parameters
    PubSubManager pubsubmgr;
    LeafNode Createdleaf;

    // ad-hoc parameter
    int timeout = 5000;


    ChatDatabase db;
    String INMCOMING = "1";

    Handler mHandler;

    // Service Methods
    // *********************************************************************

    // *******************************************************************
    // Defining XMPP Class
    public XMPP(String serverAddress, String loginUser, String passwordUser,
                String domain) {
        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.serverDomain = domain;
    }

    public String logconnected;

    public XMPPConnection getCommnection() {
        return connection;
    }

    // Connecting to Server - should call this method in MainActivity
    public Boolean connect() {
        Boolean retVal = false;
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                // pdia = new ProgressDialog();
                // pdia.setMessage("Loading...");
                // pdia.show();

            }

            @Override
            protected Boolean doInBackground(Void... arg0) {

                isConnected = false;
                ConnectionConfiguration config = new ConnectionConfiguration(
                        Constants.HOST, 5222, Constants.domain);

                // ConnectionConfiguration config = new ConnectionConfiguration(
                // "demo.zealousys.com", 5222, "@WEBSERVER");

                config.setReconnectionAllowed(true);
                config.setSecurityMode(SecurityMode.disabled);
                config.setDebuggerEnabled(true);


                connection = new XMPPTCPConnection(config);

                connectionListener = new XMPPConnectionListener();
                connection.addConnectionListener(connectionListener);
                // XMPPConnection.DEBUG_ENABLED = true;

                try {
                    connection.connect();
                    isConnected = true;

                } catch (IOException e) {
                    Log.e("error", "IO Exception error : " + e.getMessage());
                } catch (SmackException e) {
                    Log.e("error", "Smack Exception error : " + e.getMessage());
                } catch (XMPPException e) {
                    Log.e("error", "XMPP Exception error : " + e.getMessage());
                }
                return isConnected;

            }

            @Override
            protected void onPostExecute(Boolean result) {
                // print username
                String connectedusername = connection.getUser();

                System.out.println(" CONNECTED USER IS   " + connectedusername);

                Roster roster = connection.getRoster();


//                Presence availability = roster.getPresence(connectedusername);
//                Mode userMode = availability.getMode();
//
//                int user_status = retrieveState_mode(userMode, availability
//                        .isAvailable());
//
//                if (availability.getType() == Presence.Type.available)
//                {
//                    System.out.println("###########USER STATUS available  "+user_status+" "+availability
//                            .isAvailable());
//                }
//                else
//                {
//                    System.out.println("###########USER STATUS unavailable "+user_status+"  "+availability
//                            .isAvailable());
//                }


                // Listener for Chat, if someone sends msg (Start Session by
                // other user)
                chatmanager = ChatManager.getInstanceFor(connection);

                if(packetListener!=null)
                connection.removePacketListener(packetListener);
                chatmanager.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        if (!createdLocally)


                            //chat.addMessageListener(new MyMessageListener());
                            chat.addMessageListener(ApplicationController.getMessageListener());
                        try {
                            ChatStateManager.getInstance(connection).setCurrentState(ChatState.composing, chat);
                        } catch (NotConnectedException e) {
                            e.printStackTrace();
                        }

                    }
                });


//				ServiceDiscoveryManager sdm= ServiceDiscoveryManager.getInstanceFor(connection);
//
//////////////////////////////
//
//				OfflineMessageManager offlineManager = new OfflineMessageManager(
//						connection);
//				try {
//					Iterator<Message> it = (Iterator<Message>) offlineManager
//							.getMessages();
//					System.out.println(offlineManager.supportsFlexibleRetrieval());
//					System.out.println("Number of offline messages:: " + offlineManager.getMessageCount());
//					Map<String,ArrayList<Message>> offlineMsgs = new HashMap<String,ArrayList<Message>>();
//					while (it.hasNext()) {
//						org.jivesoftware.smack.packet.Message message = it.next();
//						System.out
//								.println("receive offline messages, the Received from [" + message.getFrom()
//										+ "] the message:" + message.getBody());
//						String fromUser = message.getFrom().split("/")[0];
//
//						if(offlineMsgs.containsKey(fromUser))
//						{
//							offlineMsgs.get(fromUser).add(message);
//						}else{
//							ArrayList<Message> temp = new ArrayList<Message>();
//							temp.add(message);
//							offlineMsgs.put(fromUser, temp);
//						}
//					}
//
//
//					offlineManager.deleteMessages();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}

//				PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
//				final PacketCollector collector = connection.createPacketCollector(filter);
//				connection.addPacketListener(new PacketListener() {
//
//					@Override
//					public void processPacket(Packet packet) {
//						// TODO Auto-generated method stub
//						//notification(packet.getFrom());
//						packet = collector.nextResult();
//						Message message = (Message)packet;
//						String senderName = packet.getFrom();
//
//						int alphaPOS = senderName.indexOf("@");
//						String subSenderName = senderName.substring(0,alphaPOS);
//
//
//						Log.i(TAG, "processPacket: === "+message.getBody()+" "+packet.getFrom().toString());
//
//
//					}
//
//				}, filter);


//				chatmanager.addChatListener(new ChatManagerListener() {
//					public void chatCreated(final Chat chat,
//							final boolean createdLocally) {
//						System.out.println("Chat received");
//						chat.addMessageListener(new MessageListener() {
//
//							@Override
//							public void processMessage(final Chat chat,
//									final Message message) {
//								// TODO Auto-generated method stub
//								// TODO Auto-generated method stub
//								System.out.println("In process message");
//								String from = message.getFrom();
//								String body = message.getBody().trim();
//								final String sub[] = message.getSubject().split(",");
//
//
//
//
//
//								if (from.contains("@")) {
//									from = from.substring(0, from.indexOf("@"));
//
//									// from=
//									// from.substring(0,from.indexOf("@Amigoapp.com/Smack"));
//
//									Log.i("", "Title === outer toName" + from);
//								}
//
//
//
//                                CommonClass.setFriendsChat(ApplicationController.getContext(),from,sub[0],sub[3]);
//                                System.out.println("  ####   "+from+" ****** "+sub[0]+"  &&&&&&  "+sub[3]);
//
//								db = new ChatDatabase(ApplicationController
//										.getContext());
//
//								if (!CommonClass.isForground)
//								{
//									Log.i("",
//											String.format(
//													"Received message backend '%1$s' from %2$s",
//													body, from));
//
//									db.INSERT_NEW_MESSAGE(CommonClass
//											.getChatUsername(ApplicationController
//													.getContext()), from,
//											message.getBody().trim(),
//											INMCOMING, CommonClass
//													.getCurrentTimeStamp(),
//											"false", sub[1]);
//
//
//									// if((CommonClass.getChatPref(AppController.getInstance()))
//									// == true);
//									// {
//									// System.out.println("IS CHAT REALLY ON "+
//									// CommonClass.getChatPref(AppController.getContext()));
//									if(sub.length==4)
//									{
//									createNotification(from, body, sub[0],
//											sub[1],sub[3]);
//									}
//									else
//									{
//										createNotification(from, body, sub[0],
//												sub[1],"");
//									}
//									// }
//								}
//								else
//								{
//									db.INSERT_NEW_MESSAGE(CommonClass
//											.getChatUsername(ApplicationController
//													.getContext()), from,
//											message.getBody().trim(),
//											INMCOMING, CommonClass
//													.getCurrentTimeStamp(),
//											"true", sub[1]);
//
//									Handler handler = new Handler(Looper
//											.getMainLooper());
//
//									handler.postDelayed(new Runnable() {
//
//										@Override
//										public void run() {
//											// TODO Auto-generated method stub
//											Updatelistinterface uu = ChatActivity.mFinalChatActivity;
//											System.out
//													.println("Calling refreshlist"+sub[3]);
//
//											uu.refrehsList(chat, message,sub[3]);
//										}
//									}, 1); // handler
//								} // else
//							} // process message
//						}); // msg listener
//					} // chat created
//
//				});// chat listener

                // Printing all Roster entries
                Collection<RosterEntry> entries = roster.getEntries();
                Presence presence;

                for (RosterEntry entry : entries) {
                    presence = roster.getPresence(entry.getUser());

                  /*  System.out.println(entry.getUser());
                    System.out.println(presence.getType().name());
                    System.out.println(presence.getStatus());*/

                }
                // Roster Listener,if other users's presences changed , it'll
                // print
                roster.addRosterListener(new RosterListener() {

                    @Override
                    public void presenceChanged(Presence presence) {
                        // TODO Auto-generated method stub
                        System.out.println("Presence changed: "
                                + presence.getFrom() + " " + presence);
                    }

                    @Override
                    public void entriesUpdated(Collection<String> arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void entriesDeleted(Collection<String> arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void entriesAdded(Collection<String> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

                // PubSub Node Methods
                // Create a pubsub manager using an existing XMPPConnection
                pubsubmgr = new PubSubManager(connection);

                // Register Ad-hoc commands
                try {
                    // Process root = Runtime.getRuntime().exec("su");
                    receiveAdHocCommands();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            protected void onCancelled() {
                // TODO Auto-generated method stub
                super.onCancelled();
            }

        };

        connectionThread.execute();

        // Checking if connection was successful or not
        try {
            retVal = connectionThread.get();
            System.out.println(" result is : " + retVal);
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;

    }

    private void createNotification(String from, String body, String sub,
                                    String sub2, String profile) {
        /*if (CommonClass.getChatPref(ApplicationController.getContext()) == true)
		{

          // based on whether chat notifications are on/off in settings or preference screen

		}*/
//		sub= CommonClass.strEncodeDecode(sub,true);
        NotificationManager mNotificationManager = (NotificationManager) ApplicationController
                .getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        System.out.println("-------Getting mNotificationManager: "
                + mNotificationManager);

        Intent intent = new Intent(ApplicationController.getContext(),
                ChatActivity.class);
        intent.putExtra("FROM_MESSGE", from);


        intent.putExtra("titlename", sub);
        intent.putExtra("type", sub2);
        intent.putExtra("profileimage", profile);

        Log.i("", "Notification= From=" + from);
        Log.i("", "Notification= titlename=" + sub);
        Log.e("Chat Xmpp", "Notification= body=" + body);
        Log.i("", "Notification= image=" + profile);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(
                ApplicationController.getContext(), iUniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                ApplicationController.getContext())
                .setSmallIcon(getNotificationIcon())
                .setContentTitle("" + sub)
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText(body);
//				.setTicker(body);


        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.getNotification();
        notification.contentView.setImageViewResource(android.R.id.icon, R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(
                Calendar.getInstance().get(Calendar.MILLISECOND),
                mBuilder.getNotification());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }

    public boolean isForeground(String PackageName) {

        ActivityManager manager = (ActivityManager) ApplicationController.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

        ComponentName componentInfo = task.get(0).topActivity;

        if (componentInfo.getPackageName().equals(PackageName))
            return true;

        return false;
    }

//	public int retrieveState_mode(Mode userMode, boolean isOnline) {
//		int userState = 0;
//		/** 0 for offline, 1 for online, 2 for away,3 for busy */
//		if (userMode == Mode.dnd) {
//			userState = 3;
//		} else if (userMode == Mode.away || userMode == Mode.xa) {
//			userState = 2;
//		} else if (isOnline) {
//			userState = 1;
//		}
//		return userState;
//	}

    // login in server
    private void login(XMPPConnection connection, final String loginUser,
                       final String passwordUser) {
        try {

            Log.i("", "xmpp username" + loginUser);
            Log.i("", "xmpp passwordUser" + passwordUser);
            connection.login(loginUser, passwordUser);

            setStatus(true);

            PacketFilter filter = new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class);
              packetListener = new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;


                    if (message.getBody() != null) {


                        String from = message.getFrom();
                        String body = message.getBody().trim();
                        final String sub[] = message.getSubject().split(",");


                        db = new ChatDatabase(ApplicationController
                                .getContext());




                        if (from.contains("@")) {
                            from = from.substring(0, from.indexOf("@"));

                            // from=
                            // from.substring(0,from.indexOf("@Amigoapp.com/Smack"));

                            Log.i("", "Name = " + from);
                        }

                        db.INSERT_NEW_MESSAGE(CommonClass
                                        .getChatUsername(ApplicationController
                                                .getContext()), from,
                                message.getBody().trim(),
                                INMCOMING, CommonClass
                                        .getCurrentTimeStamp(),
                                "false", sub[1]);


                        if(sub.length==4)
                        {
                            createNotification(from, body, sub[0],
                                    sub[1],sub[3]);
                        }
                        else
                        {
                            createNotification(from, body, sub[0],
                                    sub[1],"");
                        }

                        String fromName = StringUtils.parseBareAddress(message
                                .getFrom());
//                        Log.i("XMPPClient", "Got text [" + message.getBody()
//                                + "] from [" + fromName + "]");
                    }
                }
            };
            connection.addPacketListener(packetListener, filter);

        } catch (NotConnectedException e) {
            // If is not connected, a timer is schedule and a it will try to
            // reconnect
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    connect();
                }
            }, 5 * 1000);
        } catch (SaslException e) {
        } catch (XMPPException e) {
        } catch (SmackException e) {
        } catch (IOException e) {
        }
    }

    // public void createAccount( final String User,
    // final String pass2, final String fname, final String lname,
    // final String email) throws NotConnectedException {
    // // TODO Auto-generated method stub
    // // openFireConnection.chat(addressedUser2, sendmsg);
    //
    // String USERNAME = User + "@wampserver";
    // // PASSWORD = "123456";
    // String PASSWORD = pass2;
    //
    // HashMap<String, String> attr = new HashMap<String, String>();
    // attr.put("username", User);
    //
    // attr.put("name", fname + " " + lname);
    // attr.put("password", PASSWORD);
    // attr.put("email", email);
    //
    // Registration reg = new Registration();
    // reg.setType(IQ.Type.SET);
    // reg.setTo(connection.getServiceName());
    //
    // reg.setAttributes(attr);
    // PacketFilter filter = new AndFilter(new PacketIDFilter(
    // reg.getPacketID()), new PacketTypeFilter(IQ.class));
    // PacketCollector collector = connection.createPacketCollector(filter);
    // connection.sendPacket(reg);
    //
    // // AccountManager accountManager = XMPPLogic.getInstance()
    // // .getConnection().getAccountManager();
    // // accountManager.createAccount(USERNAME, PASSWORD, attr);
    // // openFireConnection.c
    // }

    // Set Presence or Status of user
    public static void setStatus(boolean available)
            throws NotConnectedException {
        // TODO Auto-generated method stub
        if (available) {
            // connection.sendPacket(new Presence(Presence.Type.available));
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("What's up? (presence status)");
            connection.sendPacket(presence);
        } else
            connection.sendPacket(new Presence(Presence.Type.unavailable));
    }

    // listener for keeping connection connect
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            if (!connection.isAuthenticated())
                login(connection, loginUser, passwordUser);
        }

        @Override
        public void authenticated(XMPPConnection arg0) {
        }

        @Override
        public void connectionClosed() {
            Log.d(TAG,
                    " [MyConnectionListener] The connection was closed normally.");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.d(TAG,
                    " [MyConnectionListener] The connection was closed due to an exception. Error:"
                            + e.getMessage());

        }

        @Override
        public void reconnectingIn(int sec) {
            Log.d(TAG,
                    " [MyConnectionListener] The connection will retry to reconnect in "
                            + sec + " seconds.");

        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.d(TAG,
                    " [MyConnectionListener] An attempt to connect to the server has failed. Error:"
                            + e.getMessage());

        }

        @Override
        public void reconnectionSuccessful() {
            Log.d(TAG,
                    " [MyConnectionListener] The connection has reconnected successfully to the server.");

        }
    }

    // Sending message to addressed user
    public void chat(String AddressedUser, String sendmsg, String type)
            throws NotConnectedException {
        // Create username whom we want to send a message
        String userToSend = AddressedUser + serverDomain;

        ChatManager chatmanager = ChatManager.getInstanceFor(connection);
        //messagelistener = new MyMessageListener();
        Chat newChat = chatmanager.createChat(userToSend,
                ApplicationController.getMessageListener());

        Log.i("", "userToSend=" + userToSend);
        try {

            Message msg = new Message(userToSend, Message.Type.chat);
            msg.setBody(sendmsg);

            String profile_pic = "";

            if (CommonClass.getUserpreference(ApplicationController.getContext()).user_image.trim().length() == 0) {
                profile_pic = "/media/bio-default.jpg";
            } else {
                profile_pic = (CommonClass.getUserpreference(ApplicationController.getContext()).user_image);
            }
            String userName = "";
            if (CommonClass.getUserpreference(ApplicationController.getContext()).first_name != null && CommonClass.getUserpreference(ApplicationController.getContext()).first_name.length() > 0)
                userName = CommonClass.getUserpreference(ApplicationController.getContext()).first_name;
            if (CommonClass.getUserpreference(ApplicationController.getContext()).last_name != null && CommonClass.getUserpreference(ApplicationController.getContext()).last_name.length() > 0)
                userName = userName + " " + CommonClass.getUserpreference(ApplicationController.getContext()).last_name;
//			System.out.println("ChatActivity.sendMessage"+userName);
            msg.setSubject(userName
                    + "," + type + "," + profile_pic);
            newChat.sendMessage(msg);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.out.println("Error Delivering block");
        }

    }

    // Adding to Roster
    public void createEntry(String user, String nickname) throws Exception {
        String rosterUsernameToAdd = user + "@" + serverDomain;
        System.out.println(String.format(
                "Creating entry for buddy '%1$s' with name %2$s",
                rosterUsernameToAdd, nickname));
        Roster roster = connection.getRoster();
        roster.createEntry(rosterUsernameToAdd, nickname, null);
    }

    // Disconnect from server
    public void disconnect() {
        if (connection != null && connection.isConnected()) {

            // connectionListener.connectionClosed();
            connection.removeConnectionListener(connectionListener);
            Presence unavailablePresence = new Presence(
                    Presence.Type.unavailable);
            try {
                // Thread.sleep(5000);
                connection.disconnect(unavailablePresence);
            } catch (NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Disconnect", "cannot disconnet: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d("Error-f",
                    "You are not connected to server so how you want to disconnect it?!");
        }
    }

    // ******************* Publish / Subscribe Functions
    // ***********************************************
    // public void createPubSubInstantNode() throws NoResponseException,
    // XMPPErrorException, NotConnectedException {
    // // Create the node
    // LeafNode leaf = pubsubmgr.createNode();
    // //return leaf;
    // }

    public void createPubSubNode(String nodeName) throws NoResponseException,
            XMPPErrorException, NotConnectedException {
        // Create the node
        ConfigureForm form = new ConfigureForm(FormType.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(false);
        form.setNotifyRetract(true);
        form.setPersistentItems(true);
        form.setPublishModel(PublishModel.open);
        Createdleaf = (LeafNode) pubsubmgr.createNode(nodeName, form);

        // return leaf;
    }

    @SuppressWarnings("unchecked")
    public void publishToPubSubNode(String nodeName)
            throws NoResponseException, XMPPErrorException,
            NotConnectedException {
        // Get the node
        LeafNode node = pubsubmgr.getNode(nodeName);

        // Publish an Item, let service set the id
        // node.send(new Item());

        // Publish an Item with the specified id
        // node.send(new Item("123abc"));

        // Publish an Item with payload
        node.send(new PayloadItem("test" + System.currentTimeMillis(),
                new SimplePayload("book", "pubsub:test:book", "Two Towers")));

    }

    public void subscribePubSubNode(String nodeName)
            throws NoResponseException, XMPPErrorException,
            NotConnectedException {

        // Get the node
        LeafNode node = pubsubmgr.getNode(nodeName);
        node.addItemEventListener(new ItemEventListener<Item>() {

            @Override
            public void handlePublishedItems(ItemPublishEvent<Item> items) {
                // TODO Auto-generated method stub

            }
        });

        node.subscribe(connection.getUser());
        Log.d("subscribe", " [pubsub] User " + connection.getUser()
                + " subscribed successfully to node " + node);
    }

    // *********************************************
    // *********** Ad-Hoc Commands Functions********
    // one for receiving , one for sending
    // **********************************************

    private void receiveAdHocCommands() throws IOException {
        // granting root permission for app
        // Process root = Runtime.getRuntime().exec("su");
        //
        // AdHocCommandManager commandManager = AdHocCommandManager
        // .getAddHocCommandsManager(connection);
        //
        // commandManager.registerCommand("first_custom_command",
        // "First Custom Command", Custom_Command.class);
        // commandManager.registerCommand("send_msg_command",
        // "Send Message Command", Custom_Command_Send.class);

        // LocalCommandFactory lcf = new LocalCommandFactory() {
        //
        // @Override
        // public LocalCommand getInstance() throws InstantiationException,
        // IllegalAccessException {
        // // TODO Auto-generated method stub
        // LocalCommand lclc = new LocalCommand() {
        //
        // @Override
        // public void prev() throws NoResponseException, XMPPErrorException,
        // NotConnectedException {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void next(Form arg0) throws NoResponseException,
        // XMPPErrorException,
        // NotConnectedException {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void execute() throws NoResponseException, XMPPErrorException,
        // NotConnectedException {
        // // TODO Auto-generated method stub
        // System.out.print("lcf");
        //
        // }
        //
        // @Override
        // public void complete(Form arg0) throws NoResponseException,
        // XMPPErrorException, NotConnectedException {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public void cancel() throws NoResponseException, XMPPErrorException,
        // NotConnectedException {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public boolean isLastStage() {
        // // TODO Auto-generated method stub
        // return false;
        // }
        //
        // @Override
        // public boolean hasPermission(String arg0) {
        // // TODO Auto-generated method stub
        // return false;
        // }
        // };
        // return lclc;
        // }
        // };
        // commandManager.registerCommand("s", "ss", lcf);

        // try {
        // commandManager.publishCommands("sender1@farmin.virtus.it");
        // } catch (XMPPException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (SmackException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    // Send AdHoc command Function
    public void sendAdHocCommands() throws XMPPException, SmackException {
        DiscoverInfo discoInfo = null;
        ServiceDiscoveryManager disco = ServiceDiscoveryManager
                .getInstanceFor(connection);
        try {
            discoInfo = disco.discoverInfo("receiver1@farmin.virtus.it/Smack");
        } catch (XMPPException e1) {
            e1.printStackTrace();
        } catch (NoResponseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Search the receiver commands and send one of them

        AdHocCommandManager commandManager = AdHocCommandManager
                .getAddHocCommandsManager(connection);
        DiscoverItems cmds = null;
        // Retrieves all the commands provided by the receiver
        cmds = commandManager
                .discoverCommands("receiver1@farmin.virtus.it/Smack");
        String commandName = null;

        // Verify the present command
        for (DiscoverItems.Item item : cmds
                .getItems()) {
            if (item.getNode().compareTo("first_custom_command") == 0) {
                commandName = item.getNode();
            }
        }
        RemoteCommand remoteCommand = null;

        // Retrieve the command to be executed
        if (commandName != null) {
            remoteCommand = commandManager.getRemoteCommand(
                    "receiver1@farmin.virtus.it/Smack", commandName);
        }
        remoteCommand.execute();
        System.out.println("Command executed. Wait " + timeout / 1000
                + " seconds...\n");
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
