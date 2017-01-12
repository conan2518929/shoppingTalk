package com.example.shoppingtalk.talk.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.R;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;

public class GroupDetailsActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;


	private EaseExpandGridView userGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
	private GridAdapter adapter;
	private ProgressDialog progressDialog;

	private RelativeLayout rl_switch_block_groupmsg;

	public static GroupDetailsActivity instance;
	
	String st = "";
	// ������������¼
	private RelativeLayout clearAllHistory;
	private RelativeLayout blacklistLayout;
	private RelativeLayout changeGroupNameLayout;
    private RelativeLayout idLayout;
    private TextView idText;
	private EaseSwitchButton switchButton;
    private GroupChangeListener groupChangeListener;
    private RelativeLayout searchLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // ��ȡ��������groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if(group == null){
            finish();
            return;
        }
        
		setContentView(R.layout.em_activity_group_details);
		instance = this;
		st = getResources().getString(R.string.people);
		clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
		userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
		blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
		changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
		idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
		idLayout.setVisibility(View.VISIBLE);
		idText = (TextView) findViewById(R.id.tv_group_id_value);
		
		rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
		switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
		searchLayout = (RelativeLayout) findViewById(R.id.rl_search); 


		idText.setText(groupId);
		if (group.getOwner() == null || "".equals(group.getOwner())
				|| !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
			blacklistLayout.setVisibility(View.GONE);
			changeGroupNameLayout.setVisibility(View.GONE);
		}
		// ����Լ���Ⱥ������ʾ��ɢ��ť
		if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.VISIBLE);
		}
		
		groupChangeListener = new GroupChangeListener();
		EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
		
		((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);
		
		List<String> members = new ArrayList<String>();
		members.addAll(group.getMembers());
		
		adapter = new GridAdapter(this, R.layout.em_grid, members);
		userGridview.setAdapter(adapter);

		// ��֤ÿ�ν����鿴���Ķ������µ�group
		updateGroup();

		// ����OnTouchListener
		userGridview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (adapter.isInDeleteMode) {
						adapter.isInDeleteMode = false;
						adapter.notifyDataSetChanged();
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		clearAllHistory.setOnClickListener(this);
		blacklistLayout.setOnClickListener(this);
		changeGroupNameLayout.setOnClickListener(this);
		rl_switch_block_groupmsg.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		String st5 = getResources().getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
		
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(GroupDetailsActivity.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// ����Ⱥ��Ա
				final String[] newmembers = data.getStringArrayExtra("newmembers");
				progressDialog.setMessage(st1);
				progressDialog.show();
				addMembersToGroup(newmembers);
				break;
			case REQUEST_CODE_EXIT: // �˳�Ⱥ
				progressDialog.setMessage(st2);
				progressDialog.show();
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // ��ɢȺ
				progressDialog.setMessage(st3);
				progressDialog.show();
				deleteGrop();
				break;

			case REQUEST_CODE_EDIT_GROUPNAME: //�޸�Ⱥ����
				final String returnData = data.getStringExtra("data");
				if(!TextUtils.isEmpty(returnData)){
					progressDialog.setMessage(st5);
					progressDialog.show();
					
					new Thread(new Runnable() {
						public void run() {
							try {
								EMClient.getInstance().groupManager().changeGroupName(groupId, returnData);
								runOnUiThread(new Runnable() {
									public void run() {
										((TextView) findViewById(R.id.group_name)).setText(returnData + "(" + group.getAffiliationsCount()
												+ st);
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st6, 0).show();
									}
								});
								
							} catch (HyphenateException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st7, 0).show();
									}
								});
							}
						}
					}).start();
				}
				break;
			default:
				break;
			}
		}
	}

    protected void addUserToBlackList(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Are_moving_to_blacklist));
        pd.show();
        new Thread(new Runnable() {
        	public void run() {
        		try {
        			EMClient.getInstance().groupManager().blockUser(groupId, username);
        			runOnUiThread(new Runnable() {
        				public void run() {
        				    refreshMembers();
        				    pd.dismiss();
        					Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, 0).show();
        				}
        			});
        		} catch (HyphenateException e) {
        			runOnUiThread(new Runnable() {
        				public void run() {
        				    pd.dismiss();
        					Toast.makeText(getApplicationContext(), R.string.failed_to_move_into, 0).show();
        				}
        			});
        		}
        	}
        }).start();
    }

	private void refreshMembers(){
	    adapter.clear();
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        adapter.addAll(members);
        
        adapter.notifyDataSetChanged();
	}
	
	/**
	 * ����˳�Ⱥ�鰴ť
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}

	/**
	 * �����ɢȺ�鰴ť
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	/**
	 * ���Ⱥ�����¼
	 */
	private void clearGroupHistory() {

		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(), EMConversationType.GroupChat);
		if (conversation != null) {
			conversation.clearAllMessages();
		}
		Toast.makeText(this, R.string.messages_are_empty, 0).show();
	}

	/**
	 * �˳�Ⱥ��
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().leaveGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * ��ɢȺ��
	 * 
	 * @param groupId
	 */
	private void deleteGrop() {
		final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().destroyGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), st5 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * ����Ⱥ��Ա
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup(final String[] newmembers) {
		final String st6 = getResources().getString(R.string.Add_group_members_fail);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					// �����ߵ���add����
					if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
						EMClient.getInstance().groupManager().addUsersToGroup(groupId, newmembers);
					} else {
						// һ���Ա����invite����
						EMClient.getInstance().groupManager().inviteUser(groupId, newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
						    refreshMembers();
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
									+ st);
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), st6 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_switch_block_groupmsg: // ���λ�ȡ������Ⱥ��
			toggleBlockGroup();
			break;

		case R.id.clear_all_history: // ��������¼
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			new EaseAlertDialog(GroupDetailsActivity.this, null, st9, null, new AlertDialogUser() {
                
                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if(confirmed){
                        clearGroupHistory();
                    }
                }
            }, true).show();
			
			break;

		case R.id.rl_blacklist: // �������б�
			startActivity(new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class).putExtra("groupId", groupId));
			break;

		case R.id.rl_change_group_name:
			startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
			break;
		case R.id.rl_search:
            startActivity(new Intent(this, GroupSearchMessageActivity.class).putExtra("groupId", groupId));
            
            break;
		default:
			break;
		}

	}

	private void toggleBlockGroup() {
		if(switchButton.isSwitchOpen()){
			EMLog.d(TAG, "change to unblock group msg");
			if (progressDialog == null) {
		        progressDialog = new ProgressDialog(GroupDetailsActivity.this);
		        progressDialog.setCanceledOnTouchOutside(false);
		    }
			progressDialog.setMessage(getString(R.string.Is_unblock));
			progressDialog.show();
			new Thread(new Runnable() {
		        public void run() {
		            try {
		                EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
		                runOnUiThread(new Runnable() {
		                    public void run() {
		                    	switchButton.closeSwitch();
		                        progressDialog.dismiss();
		                    }
		                });
		            } catch (Exception e) {
		                e.printStackTrace();
		                runOnUiThread(new Runnable() {
		                    public void run() {
		                        progressDialog.dismiss();
		                        Toast.makeText(getApplicationContext(), R.string.remove_group_of, 1).show();
		                    }
		                });
		                
		            }
		        }
		    }).start();
			
		} else {
			String st8 = getResources().getString(R.string.group_is_blocked);
			final String st9 = getResources().getString(R.string.group_of_shielding);
			EMLog.d(TAG, "change to block group msg");
			if (progressDialog == null) {
		        progressDialog = new ProgressDialog(GroupDetailsActivity.this);
		        progressDialog.setCanceledOnTouchOutside(false);
		    }
			progressDialog.setMessage(st8);
			progressDialog.show();
			new Thread(new Runnable() {
		        public void run() {
		            try {
		                EMClient.getInstance().groupManager().blockGroupMessage(groupId);
		                runOnUiThread(new Runnable() {
		                    public void run() {
		                    	switchButton.openSwitch();
		                        progressDialog.dismiss();
		                    }
		                });
		            } catch (Exception e) {
		                e.printStackTrace();
		                runOnUiThread(new Runnable() {
		                    public void run() {
		                        progressDialog.dismiss();
		                        Toast.makeText(getApplicationContext(), st9, 1).show();
		                    }
		                });
		            }
		            
		        }
		    }).start();
		}
	}

	/**
	 * Ⱥ���Աgridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		private List<String> objects;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
		    ViewHolder holder = null;
			if (convertView == null) {
			    holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
				holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
				convertView.setTag(holder);
			}else{
			    holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
			// ���һ��item�����˰�ť
			if (position == getCount() - 1) {
			    holder.textView.setText("");
				// ���ó�ɾ����ť
			    holder.imageView.setImageResource(R.drawable.em_smiley_minus_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
				// ������Ǵ����߻���û����ӦȨ�ޣ����ṩ�Ӽ��˰�ť
				if (!group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // ��ʾɾ����ť
					if (isInDeleteMode) {
						// ������ɾ��ģʽ�£�����ɾ����ť
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// ����ģʽ
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st10);
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // ����Ⱥ���Ա��ť
			    holder.textView.setText("");
			    holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
				// ������Ǵ����߻���û����ӦȨ��
				if (!group.isAllowInvites() && !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// ������ɾ��ģʽ��,�������Ӱ�ť
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st11);
							// ����ѡ��ҳ��
							startActivityForResult(
									(new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
									REQUEST_CODE_ADD_USER);
						}
					});
				}
			} else { // ��ͨitem����ʾȺ���Ա
				final String username = getItem(position);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
//				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
//				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
//				button.setCompoundDrawables(null, avatar, null, null);
				EaseUserUtils.setUserNick(username, holder.textView);
				EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
				if (isInDeleteMode) {
					// �����ɾ��ģʽ�£���ʾ����ͼ��
					convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
				}
				final String st12 = getResources().getString(R.string.not_delete_myself);
				final String st13 = getResources().getString(R.string.Are_removed);
				final String st14 = getResources().getString(R.string.Delete_failed);
				final String st15 = getResources().getString(R.string.confirm_the_members);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// �����ɾ���Լ���return
							if (EMClient.getInstance().getCurrentUser().equals(username)) {
							    new EaseAlertDialog(GroupDetailsActivity.this, st12).show();
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), 0).show();
								return;
							}
							EMLog.d("group", "remove user from group:" + username);
							deleteMembersFromGroup(username);
						} else {
							// ��������µ��user�����Խ����û������������ҳ��ȵ�
							// startActivity(new
							// Intent(GroupDetailsActivity.this,
							// ChatActivity.class).putExtra("userId",
							// user.getUsername()));

						}
					}

					/**
					 * ɾ��Ⱥ��Ա
					 * 
					 * @param username
					 */
					protected void deleteMembersFromGroup(final String username) {
						final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
						deleteDialog.setMessage(st13);
						deleteDialog.setCanceledOnTouchOutside(false);
						deleteDialog.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									// ɾ����ѡ�еĳ�Ա
								    EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);
									isInDeleteMode = false;
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											deleteDialog.dismiss();
											refreshMembers();
											((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
													+ group.getAffiliationsCount() + st);
										}
									});
								} catch (final Exception e) {
									deleteDialog.dismiss();
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(getApplicationContext(), st14 + e.getMessage(), 1).show();
										}
									});
								}

							}
						}).start();
					}
				});

				button.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
					    if(EMClient.getInstance().getCurrentUser().equals(username))
					        return true;
						if (group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
							new EaseAlertDialog(GroupDetailsActivity.this, null, st15, null, new AlertDialogUser() {
                                
                                @Override
                                public void onResult(boolean confirmed, Bundle bundle) {
                                    if(confirmed){
                                        addUserToBlackList(username);
                                    }
                                }
                            }, true).show();
							
						}
						return false;
					}
				});
			}
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount() + 2;
		}
	}

	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
				    EMClient.getInstance().groupManager().getGroupFromServer(groupId);
					
					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
									+ ")");
							loadingPB.setVisibility(View.INVISIBLE);
							refreshMembers();
							if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
								// ��ʾ��ɢ��ť
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.VISIBLE);
							} else {
								// ��ʾ�˳���ť
								exitBtn.setVisibility(View.VISIBLE);
								deleteBtn.setVisibility(View.GONE);
							}

							// update block
							EMLog.d(TAG, "group msg is blocked:" + group.isMsgBlocked());
							if (group.isMsgBlocked()) {
								switchButton.openSwitch();
							} else {
							    switchButton.closeSwitch();
							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	
	private static class ViewHolder{
	    ImageView imageView;
	    TextView textView;
	    ImageView badgeDeleteView;
	}
    
    private class GroupChangeListener implements EMGroupChangeListener{

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					refreshMembers();
				}
        		
        	});
			
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			finish();
			
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			finish();
			
		}

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // TODO Auto-generated method stub
            
        }
    	
    }

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}