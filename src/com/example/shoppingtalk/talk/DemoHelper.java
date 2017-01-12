package com.example.shoppingtalk.talk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.shoppingtalk.LoginActivity;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.UtilMap;
import com.example.shoppingtalk.talk.db.DemoDBManager;
import com.example.shoppingtalk.talk.db.InviteMessgeDao;
import com.example.shoppingtalk.talk.db.UserDao;
import com.example.shoppingtalk.talk.domain.EmojiconExampleGroupData;
import com.example.shoppingtalk.talk.domain.InviteMessage;
import com.example.shoppingtalk.talk.domain.InviteMessage.InviteMesageStatus;
import com.example.shoppingtalk.talk.domain.RobotUser;
import com.example.shoppingtalk.talk.parse.UserProfileManager;
import com.example.shoppingtalk.talk.receiver.CallReceiver;
import com.example.shoppingtalk.talk.ui.ChatActivity;
import com.example.shoppingtalk.talk.ui.LianXiRenListActivity;
import com.example.shoppingtalk.talk.ui.VideoCallActivity;
import com.example.shoppingtalk.talk.ui.VoiceCallActivity;
import com.example.shoppingtalk.talk.utils.PreferenceManager;
import com.hyphenate.easeui.controller.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.controller.EaseUI.EaseSettingsProvider;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;

public class DemoHelper {
	/**
	 * ����ͬ��listener
	 */
	static public interface DataSyncListener {
		/**
		 * ͬ�����
		 * 
		 * @param success
		 *            true���ɹ�ͬ�������ݣ�falseʧ��
		 */
		public void onSyncComplete(boolean success);
	}

	protected static final String TAG = "DemoHelper";

	private EaseUI easeUI;

	/**
	 * EMEventListener
	 */
	protected EMMessageListener messageListener = null;

	private Map<String, EaseUser> contactList;

	private Map<String, RobotUser> robotList;

	private UserProfileManager userProManager;

	private static DemoHelper instance = null;

	private DemoModel demoModel = null;

	/**
	 * HuanXin sync groups status listener
	 */
	private List<DataSyncListener> syncGroupsListeners;
	/**
	 * HuanXin sync contacts status listener
	 */
	private List<DataSyncListener> syncContactsListeners;
	/**
	 * HuanXin sync blacklist status listener
	 */
	private List<DataSyncListener> syncBlackListListeners;

	private boolean isSyncingGroupsWithServer = false;
	private boolean isSyncingContactsWithServer = false;
	private boolean isSyncingBlackListWithServer = false;
	private boolean isGroupsSyncedWithServer = false;
	private boolean isContactsSyncedWithServer = false;
	private boolean isBlackListSyncedWithServer = false;

	private boolean alreadyNotified = false;

	public boolean isVoiceCalling;
	public boolean isVideoCalling;

	private String username;

	private Context appContext;

	private CallReceiver callReceiver;

	private EMConnectionListener connectionListener;

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	private LocalBroadcastManager broadcastManager;

	private boolean isGroupAndContactListenerRegisted;

	private Handler h = new Handler();
	private Map<String, String> map = UtilMap.getInstance().init();
	private DemoHelper() {
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		demoModel = new DemoModel(context);
		EMOptions options = initChatOptions();
		// options��null��ʹ��Ĭ�ϵ�
		if (EaseUI.getInstance().init(context, options)) {
			appContext = context;

			// ��Ϊ����ģʽ�������ʽ��ʱ�������Ϊfalse���������Ķ������Դ
			EMClient.getInstance().setDebugMode(true);
			// get easeui instance
			easeUI = EaseUI.getInstance();
			// ����easeui��api����providers
			setEaseUIProviders();
			// ��ʼ��PreferenceManager
			PreferenceManager.init(context);
			// ��ʼ���û�������
			getUserProfileManager().init(context);

			// ����ȫ�ּ���
			setGlobalListeners();
			broadcastManager = LocalBroadcastManager.getInstance(appContext);
			initDbDao();
		}
	}

	private EMOptions initChatOptions() {
		Log.d(TAG, "init HuanXin Options");

		// ��ȡ��EMChatOptions����
		EMOptions options = new EMOptions();
		// Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
		options.setAcceptInvitationAlways(false);
		// �����Ƿ���Ҫ�Ѷ���ִ
		options.setRequireAck(true);
		// �����Ƿ���Ҫ���ʹ��ִ
		options.setRequireDeliveryAck(false);

		// ʹ��gcm��mipushʱ��������Ĳ����滻���Լ�app�����
		// ����google���ͣ���Ҫ��GCM��app�������ô˲���
		options.setGCMNumber("324169311137");
		// ��С���ֻ��ϵ�app��killʱʹ��С�����ͽ�����Ϣ��ʾ��ͬGCMһ�����Ǳ����
		options.setMipushConfig("2882303761517426801", "5381742660801");
		// ���ɻ�Ϊ����ʱ��Ҫ����
		// options.setHuaweiPushAppId("10492024");

		options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
		options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
		options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());

		return options;
	}
	
	protected void setEaseUIProviders() {
		// ��Ҫeaseui����ʾ�û�ͷ����ǳ����ô�provider
		easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

			@Override
			public EaseUser getUser(String username) {
				return getUserInfo(username);
			}
		});

		// �����ã���ʹ��easeuiĬ�ϵ�
		easeUI.setSettingsProvider(new EaseSettingsProvider() {

			@Override
			public boolean isSpeakerOpened() {
				return demoModel.getSettingMsgSpeaker();
			}

			@Override
			public boolean isMsgVibrateAllowed(EMMessage message) {
				return demoModel.getSettingMsgVibrate();
			}

			@Override
			public boolean isMsgSoundAllowed(EMMessage message) {
				return demoModel.getSettingMsgSound();
			}

			@Override
			public boolean isMsgNotifyAllowed(EMMessage message) {
				if (message == null) {
					return demoModel.getSettingMsgNotification();
				}
				if (!demoModel.getSettingMsgNotification()) {
					return false;
				} else {
					// �����������Ϣ��ʾ
					// ���ε��û���Ⱥ�鲻��ʾ�û�
					String chatUsename = null;
					List<String> notNotifyIds = null;
					// ��ȡ���õĲ���ʾ����Ϣ���û�����Ⱥ��ids
					if (message.getChatType() == ChatType.Chat) {
						chatUsename = message.getFrom();
						notNotifyIds = demoModel.getDisabledIds();
					} else {
						chatUsename = message.getTo();
						notNotifyIds = demoModel.getDisabledGroups();
					}

					if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
						return true;
					} else {
						return false;
					}
				}
			}
		});
		// ���ñ���provider
		easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {

			@Override
			public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
				EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
				for (EaseEmojicon emojicon : data.getEmojiconList()) {
					if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
						return emojicon;
					}
				}
				return null;
			}

			@Override
			public Map<String, Object> getTextEmojiconMapping() {
				// �������ֱ���emoji�ı���ͼƬ(resource id���߱���·��)��ӳ��map
				return null;
			}
		});

		// �����ã���ʹ��easeuiĬ�ϵ�
		easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// �޸ı���,����ʹ��Ĭ��
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// ����Сͼ�꣬����ΪĬ��
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// ����״̬������Ϣ��ʾ�����Ը���message����������Ӧ��ʾ
				String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[����]");
				}
				EaseUser user = getUserInfo(message.getFrom());
				if (user != null) {
					return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
				} else {
					return message.getFrom() + ": " + ticker;
				}
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
				return null;
				// return fromUsersNum + "�����ѣ�������" + messageNum + "����Ϣ";
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// ���õ��֪ͨ����ת�¼�
				Intent intent = new Intent(appContext, ChatActivity.class);
				// �е绰ʱ������ת��ͨ��ҳ��
				if (isVideoCalling) {
					intent = new Intent(appContext, VideoCallActivity.class);
				} else if (isVoiceCalling) {
					intent = new Intent(appContext, VoiceCallActivity.class);
				} else {
					ChatType chatType = message.getChatType();
					if (chatType == ChatType.Chat) { // ������Ϣ
						intent.putExtra("userId", message.getFrom());
						intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
					} else { // Ⱥ����Ϣ
						// message.getTo()ΪȺ��id
						intent.putExtra("userId", message.getTo());
						if (chatType == ChatType.GroupChat) {
							intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
						} else {
							intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
						}
					}
				}
				return intent;
			}
		});
	}

	/**
	 * ����ȫ���¼�����
	 */
	protected void setGlobalListeners() {
		syncGroupsListeners = new ArrayList<DataSyncListener>();
		syncContactsListeners = new ArrayList<DataSyncListener>();
		syncBlackListListeners = new ArrayList<DataSyncListener>();

		isGroupsSyncedWithServer = demoModel.isGroupsSynced();
		isContactsSyncedWithServer = demoModel.isContactSynced();
		isBlackListSyncedWithServer = demoModel.isBacklistSynced();

		// create the global connection listener
		connectionListener = new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {
				if (error == EMError.USER_REMOVED) {
					onCurrentAccountRemoved();
				} else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
					onConnectionConflict();
				}
			}

			@Override
			public void onConnected() {

				// in case group and contact were already synced, we supposed to
				// notify sdk we are ready to receive the events
				if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
					new Thread() {
						@Override
						public void run() {
							DemoHelper.getInstance().notifyForRecevingEvents();
						}
					}.start();
				} else {
					if (!isGroupsSyncedWithServer) {
						asyncFetchGroupsFromServer(null);
					}

					if (!isContactsSyncedWithServer) {
						asyncFetchContactsFromServer(null);
					}

					if (!isBlackListSyncedWithServer) {
						asyncFetchBlackListFromServer(null);
					}
				}
			}
		};

		IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
		if (callReceiver == null) {
			callReceiver = new CallReceiver();
		}

		// ע��ͨ���㲥������
		appContext.registerReceiver(callReceiver, callFilter);
		// ע�����Ӽ���
		EMClient.getInstance().addConnectionListener(connectionListener);
		// ע��Ⱥ�����ϵ�˼���
		registerGroupAndContactListener();
		// ע����Ϣ�¼�����
		registerEventListener();

	}

	private void initDbDao() {
		inviteMessgeDao = new InviteMessgeDao(appContext);
		userDao = new UserDao(appContext);
	}

	/**
	 * ע��Ⱥ�����ϵ�˼���������logout��ʱ��ᱻsdk��������ٴε�¼��ʱ����Ҫ��ע��һ��
	 */
	public void registerGroupAndContactListener() {
		if (!isGroupAndContactListenerRegisted) {
			// ע��Ⱥ��䶯����
			EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
			// ע����ϵ�˱䶯����
			EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
			isGroupAndContactListenerRegisted = true;
		}

	}

	/**
	 * Ⱥ��䶯����
	 */
	class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// �û��������Ⱥ��
			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			msg.setGroupInviter(inviter);
			Log.d(TAG, "�յ��������Ⱥ�ģ�" + groupName);
			msg.setStatus(InviteMesageStatus.GROUPINVITATION);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationAccpted(String groupId, String invitee, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// �Է�ͬ���Ⱥ����
			boolean hasGroup = false;
			EMGroup _group = null;
			for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					_group = group;
					break;
				}
			}
			if (!hasGroup)
				return;

			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(_group == null ? groupId : _group.getGroupName());
			msg.setReason(reason);
			msg.setGroupInviter(invitee);
			Log.d(TAG, invitee + "ͬ�����Ⱥ�ģ�" + _group == null ? groupId : _group.getGroupName());
			msg.setStatus(InviteMesageStatus.GROUPINVITATION_ACCEPTED);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

			new InviteMessgeDao(appContext).deleteMessage(groupId);

			// �Է�ͬ���Ⱥ����
			boolean hasGroup = false;
			EMGroup group = null;
			for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (_group.getGroupId().equals(groupId)) {
					group = _group;
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			InviteMessage msg = new InviteMessage();
			msg.setFrom(groupId);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(group == null ? groupId : group.getGroupName());
			msg.setReason(reason);
			msg.setGroupInviter(invitee);
			Log.d(TAG, invitee + "�ܾ�����Ⱥ�ģ�" + group == null ? groupId : group.getGroupName());
			msg.setStatus(InviteMesageStatus.GROUPINVITATION_DECLINED);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// TODO ��ʾ�û���T�ˣ�demoʡ�Դ˲���
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// Ⱥ����ɢ
			// TODO ��ʾ�û�Ⱥ����ɢ,demoʡ��
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

			// �û��������Ⱥ��
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " �������Ⱥ�ģ�" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {

			String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
			// ��Ⱥ���뱻ͬ��
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(accepter + " " + st4));
			msg.setStatus(Status.SUCCESS);
			// ����ͬ����Ϣ
			EMClient.getInstance().chatManager().saveMessage(msg);
			// ��������Ϣ
			getNotifier().viberateAndPlayTone(msg);

			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// ��Ⱥ���뱻�ܾ���demoδʵ��
		}

		@Override
		public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
			// ������
			String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(inviter + " " + st3));
			msg.setStatus(EMMessage.Status.SUCCESS);
			// ����������Ϣ
			EMClient.getInstance().chatManager().saveMessage(msg);
			// ��������Ϣ
			getNotifier().viberateAndPlayTone(msg);
			EMLog.d(TAG, "onAutoAcceptInvitationFromGroup groupId:" + groupId);
			// ����local�㲥
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
		}
	}

	/***
	 * ���ѱ仯listener
	 * 
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(String username) {
			// �������ӵ���ϵ��
			Map<String, EaseUser> localUsers = getContactList();
			Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
			EaseUser user = new EaseUser(username);

			// ��Ӻ���ʱ���ܻ�ص�added��������
			if (!localUsers.containsKey(username)) {
				userDao.saveContact(user);
			}
			toAddUsers.put(username, user);
			localUsers.putAll(toAddUsers);

			// ���ͺ��ѱ䶯�㲥
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactDeleted(String username) {
			// ��ɾ��
			Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
			localUsers.remove(username);
			userDao.deleteContact(username);
			inviteMessgeDao.deleteMessage(username);

			// ���ͺ��ѱ䶯�㲥
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactInvited(String username, String reason) {
			// �ӵ��������Ϣ�����������(ͬ���ܾ�)�����ߺ󣬷��������Զ��ٷ����������Կͻ��˲���Ҫ�ظ�����
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// �Լ���װ��javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "�������Ϊ����,reason: " + reason);
			// ������Ӧstatus
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// �Լ���װ��javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "ͬ������ĺ�������");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);
			broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactRefused(String username) {
			// �ο�ͬ�⣬������ʵ�ִ˹���,demoδʵ��
			Log.d(username, username + "�ܾ�����ĺ�������");
		}
	}

	/**
	 * ���沢��ʾ��Ϣ��������Ϣ
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		if (inviteMessgeDao == null) {
			inviteMessgeDao = new InviteMessgeDao(appContext);
		}
		inviteMessgeDao.saveMessage(msg);
		// ����δ����������û�о�ȷ����
		inviteMessgeDao.saveUnreadMessageCount(1);
		// ��ʾ������Ϣ
		getNotifier().viberateAndPlayTone(null);
	}

	/**
	 * �˺��ڱ���豸��¼
	 */
	protected void onConnectionConflict() {
		Intent intent = new Intent(appContext, LianXiRenListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
		appContext.startActivity(intent);
	}

	/**
	 * �˺ű��Ƴ�
	 */
	protected void onCurrentAccountRemoved() {
		Intent intent = new Intent(appContext, LianXiRenListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constant.ACCOUNT_REMOVED, true);
		appContext.startActivity(intent);
	}

	@SuppressWarnings("rawtypes")
	private EaseUser getUserInfo(String username) {
		// ��ȡuser��Ϣ��demo�Ǵ��ڴ�ĺ����б����ȡ��
		// ʵ�ʿ����У����ܻ���Ҫ�ӷ�������ȡ�û���Ϣ,
		// �ӷ�������ȡ�����ݣ���û�������������Ƶ������������
		EaseUser user = null;
		Iterator keys = map.keySet().iterator();
		if (username == null) {
			EMLog.d(TAG, "username is null when get user info");
			return null;
		}
		if (username.equals(EMClient.getInstance().getCurrentUser())){//�����Լ���
			user = new EaseUser(username);
		}else{//���öԷ����ǳƺ�ͷ��
			user = new EaseUser(username);
//			user.setAvatar("");
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (username.equals(key)) {
					user.setNick(map.get(username));
				}
			}
		}
		return user;
	}

	/**
	 * ȫ���¼����� ��Ϊ���ܻ���UIҳ���ȴ��������Ϣ������һ�����UIҳ���Ѿ���������Ͳ���Ҫ�ٴδ��� activityList.size()
	 * <= 0 ��ζ������ҳ�涼�Ѿ��ں�̨���У������Ѿ��뿪Activity Stack
	 */
	protected void registerEventListener() {
		messageListener = new EMMessageListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
					// Ӧ���ں�̨������Ҫˢ��UI,֪ͨ����ʾ����Ϣ
					if (!easeUI.hasForegroundActivies()) {
//						Intent intent = new Intent();
//						intent.setAction("com.app.action.broadcast");
//						intent.putExtra("id", message.getFrom());
//						appContext.sendBroadcast(intent);
						getNotifier().onNewMsg(message);
					}
				}
			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					System.out.println("sssssssss");
					EMLog.d(TAG, "�յ�͸����Ϣ");
					// ��ȡ��Ϣbody
					EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
					final String action = cmdMsgBody.action();// ��ȡ�Զ���action

					// ��ȡ��չ���� �˴�ʡ��
					// message.getStringAttribute("");
					EMLog.d(TAG, String.format("͸����Ϣ��action:%s,message:%s", action, message.toString()));
					final String str = appContext.getString(R.string.receive_the_passthrough);

					final String CMD_TOAST_BROADCAST = "hyphenate.demo.cmd.toast";
					IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

					if (broadCastReceiver == null) {
						broadCastReceiver = new BroadcastReceiver() {

							@Override
							public void onReceive(Context context, Intent intent) {
								// TODO Auto-generated method stub
								Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
							}
						};

						// ע��㲥������
						appContext.registerReceiver(broadCastReceiver, cmdFilter);
					}

					Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
					broadcastIntent.putExtra("cmd_value", str + action);
					appContext.sendBroadcast(broadcastIntent, null);
				}
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> messages) {
			}

			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> message) {
			}

			@Override
			public void onMessageChanged(EMMessage message, Object change) {

			}
		};

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	/**
	 * �Ƿ��¼�ɹ���
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}

	/**
	 * �˳���¼
	 * 
	 * @param unbindDeviceToken
	 *            �Ƿ����豸token(ʹ��GCM����)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		// endCall();
		Log.d(TAG, "logout: " + unbindDeviceToken);
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}

	/**
	 * ��ȡ��Ϣ֪ͨ��
	 * 
	 * @return
	 */
	public EaseNotifier getNotifier() {
		return easeUI.getNotifier();
	}

	public DemoModel getModel() {
		return (DemoModel) demoModel;
	}

	/**
	 * ���ú���user list���ڴ���
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, EaseUser> aContactList) {
		if (aContactList == null) {
			if (contactList != null) {
				contactList.clear();
			}
			return;
		}

		contactList = aContactList;
	}

	/**
	 * ���浥��user
	 */
	public void saveContact(EaseUser user) {
		contactList.put(user.getUsername(), user);
		demoModel.saveContact(user);
	}

	/**
	 * ��ȡ����list
	 * 
	 * @return
	 */
	public Map<String, EaseUser> getContactList() {
		if (isLoggedIn() && contactList == null) {
			contactList = demoModel.getContactList();
		}

		// return a empty non-null object to avoid app crash
		if (contactList == null) {
			return new Hashtable<String, EaseUser>();
		}

		return contactList;
	}

	/**
	 * ���õ�ǰ�û��Ļ���id
	 * 
	 * @param username
	 */
	public void setCurrentUserName(String username) {
		this.username = username;
		demoModel.setCurrentUserName(username);
	}

	/**
	 * ��ȡ��ǰ�û��Ļ���id
	 */
	public String getCurrentUsernName() {
		if (username == null) {
			username = demoModel.getCurrentUsernName();
		}
		return username;
	}

	public void setRobotList(Map<String, RobotUser> robotList) {
		this.robotList = robotList;
	}

	public Map<String, RobotUser> getRobotList() {
		if (isLoggedIn() && robotList == null) {
			robotList = demoModel.getRobotList();
		}
		return robotList;
	}

	/**
	 * update user list to cach And db
	 * 
	 * @param contactList
	 */
	public void updateContactList(List<EaseUser> contactInfoList) {
		for (EaseUser u : contactInfoList) {
			contactList.put(u.getUsername(), u);
		}
		ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
		mList.addAll(contactList.values());
		demoModel.saveContactList(mList);
	}

	public UserProfileManager getUserProfileManager() {
		if (userProManager == null) {
			userProManager = new UserProfileManager();
		}
		return userProManager;
	}

	void endCall() {
		try {
			EMClient.getInstance().callManager().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.add(listener);
		}
	}

	public void removeSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.remove(listener);
		}
	}

	public void addSyncContactListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactsListeners.contains(listener)) {
			syncContactsListeners.add(listener);
		}
	}

	public void removeSyncContactListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactsListeners.contains(listener)) {
			syncContactsListeners.remove(listener);
		}
	}

	public void addSyncBlackListListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncBlackListListeners.contains(listener)) {
			syncBlackListListeners.add(listener);
		}
	}

	public void removeSyncBlackListListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncBlackListListeners.contains(listener)) {
			syncBlackListListeners.remove(listener);
		}
	}

	/**
	 * ͬ���������ӷ�������ȡȺ���б� �÷������¼����״̬������ͨ��isSyncingGroupsFromServer��ȡ�Ƿ����ڸ���
	 * ��isGroupsSyncedWithServer��ȡ�Ƿ�����Ѿ����
	 * 
	 * @throws EaseMobException
	 */
	public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
		if (isSyncingGroupsWithServer) {
			return;
		}

		isSyncingGroupsWithServer = true;

		new Thread() {
			@Override
			public void run() {
				try {
					EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isGroupsSyncedWithServer = false;
						isSyncingGroupsWithServer = false;
						// ֪ͨlistenerͬ��Ⱥ�����
						noitifyGroupSyncListeners(false);
						return;
					}

					demoModel.setGroupsSynced(true);

					isGroupsSyncedWithServer = true;
					isSyncingGroupsWithServer = false;

					// ֪ͨlistenerͬ��Ⱥ�����
					noitifyGroupSyncListeners(true);
					if (isContactsSyncedWithServer()) {
						notifyForRecevingEvents();
					}
					if (callback != null) {
						callback.onSuccess();
					}
				} catch (HyphenateException e) {
					demoModel.setGroupsSynced(false);
					isGroupsSyncedWithServer = false;
					isSyncingGroupsWithServer = false;
					noitifyGroupSyncListeners(false);
					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}
			}
		}.start();
	}

	public void noitifyGroupSyncListeners(boolean success) {
		for (DataSyncListener listener : syncGroupsListeners) {
			listener.onSyncComplete(success);
		}
	}

	public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback) {
		if (isSyncingContactsWithServer) {
			return;
		}

		isSyncingContactsWithServer = true;

		new Thread() {
			@Override
			public void run() {
				List<String> usernames = null;
				try {
					usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isContactsSyncedWithServer = false;
						isSyncingContactsWithServer = false;
						// ֪ͨlisteners��ϵ��ͬ�����
						notifyContactsSyncListener(false);
						return;
					}
					Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
					for (String username : usernames) {
						EaseUser user = new EaseUser(username);
						EaseCommonUtils.setUserInitialLetter(user);
						userlist.put(username, user);
					}
					// �����ڴ�
					getContactList().clear();
					getContactList().putAll(userlist);
					// ����db
					UserDao dao = new UserDao(appContext);
					List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
					dao.saveContactList(users);

					demoModel.setContactSynced(true);
					EMLog.d(TAG, "set contact syn status to true");

					isContactsSyncedWithServer = true;
					isSyncingContactsWithServer = false;

					// ֪ͨlisteners��ϵ��ͬ�����
					notifyContactsSyncListener(true);
					if (isGroupsSyncedWithServer()) {
						notifyForRecevingEvents();
					}
					getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new EMValueCallBack<List<EaseUser>>() {

						@Override
						public void onSuccess(List<EaseUser> uList) {
							updateContactList(uList);
							getUserProfileManager().notifyContactInfosSyncListener(true);
						}

						@Override
						public void onError(int error, String errorMsg) {

						}
					});
					if (callback != null) {
						callback.onSuccess(usernames);
					}
				} catch (HyphenateException e) {
					demoModel.setContactSynced(false);
					isContactsSyncedWithServer = false;
					isSyncingContactsWithServer = false;
					notifyContactsSyncListener(false);
					e.printStackTrace();
					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void notifyContactsSyncListener(boolean success) {
		for (DataSyncListener listener : syncContactsListeners) {
			listener.onSyncComplete(success);
		}
	}

	public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {

		if (isSyncingBlackListWithServer) {
			return;
		}

		isSyncingBlackListWithServer = true;

		new Thread() {
			@Override
			public void run() {
				try {
					List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();

					// in case that logout already before server returns, we
					// should return immediately
					if (!isLoggedIn()) {
						isBlackListSyncedWithServer = false;
						isSyncingBlackListWithServer = false;
						notifyBlackListSyncListener(false);
						return;
					}

					demoModel.setBlacklistSynced(true);

					isBlackListSyncedWithServer = true;
					isSyncingBlackListWithServer = false;

					notifyBlackListSyncListener(true);
					if (callback != null) {
						callback.onSuccess(usernames);
					}
				} catch (HyphenateException e) {
					demoModel.setBlacklistSynced(false);

					isBlackListSyncedWithServer = false;
					isSyncingBlackListWithServer = true;
					e.printStackTrace();

					if (callback != null) {
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void notifyBlackListSyncListener(boolean success) {
		for (DataSyncListener listener : syncBlackListListeners) {
			listener.onSyncComplete(success);
		}
	}

	public boolean isSyncingGroupsWithServer() {
		return isSyncingGroupsWithServer;
	}

	public boolean isSyncingContactsWithServer() {
		return isSyncingContactsWithServer;
	}

	public boolean isSyncingBlackListWithServer() {
		return isSyncingBlackListWithServer;
	}

	public boolean isGroupsSyncedWithServer() {
		return isGroupsSyncedWithServer;
	}

	public boolean isContactsSyncedWithServer() {
		return isContactsSyncedWithServer;
	}

	public boolean isBlackListSyncedWithServer() {
		return isBlackListSyncedWithServer;
	}

	public synchronized void notifyForRecevingEvents() {
		if (alreadyNotified) {
			return;
		}

		// ֪ͨsdk��UI �Ѿ���ʼ����ϣ�ע������Ӧ��receiver��listener, ���Խ���broadcast��
		alreadyNotified = true;
	}

	synchronized void reset() {
		isSyncingGroupsWithServer = false;
		isSyncingContactsWithServer = false;
		isSyncingBlackListWithServer = false;

		demoModel.setGroupsSynced(false);
		demoModel.setContactSynced(false);
		demoModel.setBlacklistSynced(false);

		isGroupsSyncedWithServer = false;
		isContactsSyncedWithServer = false;
		isBlackListSyncedWithServer = false;

		alreadyNotified = false;
		isGroupAndContactListenerRegisted = false;

		setContactList(null);
		setRobotList(null);
		getUserProfileManager().reset();
		DemoDBManager.getInstance().closeDB();
	}

	public void pushActivity(Activity activity) {
		easeUI.pushActivity(activity);
	}

	public void popActivity(Activity activity) {
		easeUI.popActivity(activity);
	}
}
