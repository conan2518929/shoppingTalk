package com.example.shoppingtalk.talk.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.shoppingtalk.BaseActivity;
import com.example.shoppingtalk.R;
import com.example.shoppingtalk.talk.Constant;
import com.example.shoppingtalk.talk.DemoHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseSidebar;

public class GroupPickContactsActivity extends BaseActivity {
	private ListView listView;
	/** �Ƿ�Ϊһ���½���Ⱥ�� */
	protected boolean isCreatingNewGroup;
	/** �Ƿ�Ϊ��ѡ */
	private boolean isSignleChecked;
	private PickContactAdapter contactAdapter;
	/** group��һ��ʼ���еĳ�Ա */
	private List<String> exitingMembers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_group_pick_contacts);

		// String groupName = getIntent().getStringExtra("groupName");
		String groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// ����Ⱥ��
			isCreatingNewGroup = true;
		} else {
			// ��ȡ��Ⱥ��ĳ�Ա�б�
			EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
			exitingMembers = group.getMembers();
		}
		if(exitingMembers == null)
			exitingMembers = new ArrayList<String>();
		// ��ȡ�����б�
		final List<EaseUser> alluserList = new ArrayList<EaseUser>();
		for (EaseUser user : DemoHelper.getInstance().getContactList().values()) {
			if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME) & !user.getUsername().equals(Constant.CHAT_ROOM) & !user.getUsername().equals(Constant.CHAT_ROBOT))
				alluserList.add(user);
		}
		// ��list��������
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNick().compareTo(rhs.getNick());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
                
            }
        });

		listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox, alluserList);
		listView.setAdapter(contactAdapter);
		((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();

			}
		});
	}

	/**
	 * ȷ��ѡ���members
	 * 
	 * @param v
	 */
	public void save(View v) {
		setResult(RESULT_OK, new Intent().putExtra("newmembers", getToBeAddMembers().toArray(new String[0])));
		finish();
	}

	/**
	 * ��ȡҪ����ӵĳ�Ա
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> members = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			String username = contactAdapter.getItem(i).getUsername();
			if (contactAdapter.isCheckedArray[i] && !exitingMembers.contains(username)) {
				members.add(username);
			}
		}

		return members;
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends EaseContactAdapter {

		private boolean[] isCheckedArray;

		public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
//			if (position > 0) {
				final String username = getItem(position).getUsername();
				// ѡ���checkbox
				final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
				TextView nameView = (TextView) view.findViewById(R.id.name);
				
				if (checkBox != null) {
				    if(exitingMembers != null && exitingMembers.contains(username)){
	                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_gray_selector);
	                }else{
	                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_selector);
	                }
					// checkBox.setOnCheckedChangeListener(null);

					checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// Ⱥ����ԭ���ĳ�Աһֱ��Ϊѡ��״̬
							if (exitingMembers.contains(username)) {
									isChecked = true;
									checkBox.setChecked(true);
							}
							isCheckedArray[position] = isChecked;
							//����ǵ�ѡģʽ
							if (isSignleChecked && isChecked) {
								for (int i = 0; i < isCheckedArray.length; i++) {
									if (i != position) {
										isCheckedArray[i] = false;
									}
								}
								contactAdapter.notifyDataSetChanged();
							}
							
						}
					});
					// Ⱥ����ԭ���ĳ�Աһֱ��Ϊѡ��״̬
					if (exitingMembers.contains(username)) {
							checkBox.setChecked(true);
							isCheckedArray[position] = true;
					} else {
						checkBox.setChecked(isCheckedArray[position]);
					}
				}
//			}
			return view;
		}
	}

	public void back(View view){
		finish();
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