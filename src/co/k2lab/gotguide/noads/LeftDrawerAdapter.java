package co.k2lab.gotguide.noads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeftDrawerAdapter extends BaseExpandableListAdapter {
	private static final int[] GROUPS = {R.id.left_drawer_group_tab, R.id.left_drawer_group_settings, R.id.left_drawer_group_hbo, R.id.left_drawer_group_social};
	
	private static final int[] GROUP_TAB = {R.id.left_drawer_group_tab_item_home, R.id.left_drawer_group_tab_item_map, R.id.left_drawer_group_tab_item_houses, R.id.left_drawer_group_tab_item_people, R.id.left_drawer_group_tab_item_appendix};
	private static final int[] GROUP_SETTINGS = {R.id.left_drawer_group_settings_item_language, R.id.left_drawer_group_settings_item_spoiler};
	private static final int[] GROUP_HBO = {R.id.left_drawer_group_hbo_item_com, R.id.left_drawer_group_hbo_item_go, R.id.left_drawer_group_hbo_item_connect, R.id.left_drawer_group_hbo_item_store};
	private static final int[] GROUP_SOCIAL = {R.id.left_drawer_group_social_item_fb, R.id.left_drawer_group_social_item_tb, R.id.left_drawer_group_social_item_tw, R.id.left_drawer_group_social_item_yt, R.id.left_drawer_group_social_item_ig};

	private MainActivity mMainActivity;
	
	public LeftDrawerAdapter(MainActivity activity) {
		this.mMainActivity = activity;
	}
	
	private boolean mShouldShowSettings = false;
	private boolean mIsLanguageEn = true;
	private boolean mIsSpoilerAlertOn = true;

	@Override
	public int getGroupCount() {
		mShouldShowSettings = mMainActivity.isSettingsReady();
		mIsLanguageEn = mMainActivity.isLanguageEn();
		mIsSpoilerAlertOn = mMainActivity.isSpoilerAlertOn();
		return GROUPS.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		long groupId = getGroupId(groupPosition);
		if (groupId == R.id.left_drawer_group_hbo)
			return 4;
		else if (groupId == R.id.left_drawer_group_settings)
			return 2;
		else
			return 5;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return GROUPS[groupPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		long groupId = getGroupId(groupPosition);
		if (groupId == R.id.left_drawer_group_tab)
			return GROUP_TAB[childPosition];
		else if (groupId == R.id.left_drawer_group_settings)
			return GROUP_SETTINGS[childPosition];
		else if (groupId == R.id.left_drawer_group_hbo)
			return GROUP_HBO[childPosition];
		else
			return GROUP_SOCIAL[childPosition];
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.mMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.left_list_group, null);
		
		String groupName;
		long groupId = getGroupId(groupPosition);
		if (groupId == R.id.left_drawer_group_tab)
			groupName = mMainActivity.getResources().getString(R.string.in_this_episode);
		else if (groupId == R.id.left_drawer_group_settings)
			groupName = mMainActivity.getResources().getString(R.string.settings);
		else {
			if (groupId == R.id.left_drawer_group_hbo)
				groupName = mMainActivity.getResources().getString(R.string.HBO);
			else
				groupName = mMainActivity.getResources().getString(R.string.social);
			
			ImageView indicator = ((ImageView) convertView.findViewById(R.id.group_indicator));
			indicator.setVisibility(View.VISIBLE);
			if (isExpanded) {
				indicator.setImageResource(R.drawable.drawer_top_arrow);
			} else {
				indicator.setImageResource(R.drawable.drawer_bottom_arrow);
			}
		}	
		
		((TextView) convertView.findViewById(R.id.group_textview))
				.setText(groupName);		

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.mMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		long groupId = getGroupId(groupPosition);
		long childId = getChildId(groupPosition, childPosition);
		
		if (groupId == R.id.left_drawer_group_tab) {
			convertView = inflater.inflate(R.layout.left_list_item, null);
			
			int imageId, titleId;
			if (childId == R.id.left_drawer_group_tab_item_appendix) {
				imageId = R.drawable.drawer_appendix;
				titleId = R.string.appendix;
			} else if (childId == R.id.left_drawer_group_tab_item_home) {
				imageId = R.drawable.drawer_home;
				titleId = R.string.home;
			} else if (childId == R.id.left_drawer_group_tab_item_houses) {
				imageId = R.drawable.drawer_houses;
				titleId = R.string.houses;
			} else if (childId == R.id.left_drawer_group_tab_item_map) {
				imageId = R.drawable.drawer_map;
				titleId = R.string.map;
			} else {
				imageId = R.drawable.drawer_people;
				titleId = R.string.people;
			}
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.child_icon);
			icon.setVisibility(View.VISIBLE);
			icon.setImageResource(imageId);
			
			((TextView)convertView.findViewById(R.id.child_textview)).setText(mMainActivity.getResources().getString(titleId));
		}
		
		else if (groupId == R.id.left_drawer_group_settings) {
			convertView  = inflater.inflate(R.layout.left_list_settings_item, null);
			String title, subtitle;
			if (childId == R.id.left_drawer_group_settings_item_language) {
				title = mMainActivity.getResources().getString(R.string.language);
				subtitle = mShouldShowSettings ?
						(mIsLanguageEn ? "English" : "Español") :
						mMainActivity.getResources().getString(R.string.waiting);
			}
			else {
				title = mMainActivity.getResources().getString(R.string.spoiler_alerts);
				subtitle = mMainActivity.getResources().getString(mShouldShowSettings ?
						(mIsSpoilerAlertOn ? R.string.on : R.string.off) :
						R.string.waiting);
			}
			
			((TextView)convertView.findViewById(R.id.textview)).setText(title);
			((TextView)convertView.findViewById(R.id.textview_change)).setText(subtitle);
		}
		
		else {
			convertView = inflater.inflate(R.layout.left_list_item, null);
			convertView.findViewById(R.id.child_link_icon).setVisibility(View.VISIBLE);
			
			String title = null;
			if (groupId == R.id.left_drawer_group_hbo) {
				if (childId == R.id.left_drawer_group_hbo_item_com)
					title = "HBO.com";
				else if (childId == R.id.left_drawer_group_hbo_item_go)
					title = "HBO Go";
				else if (childId == R.id.left_drawer_group_hbo_item_connect)
					title = "HBO Connect";
				else
					title = "HBO Store";
			} else {
				if (childId == R.id.left_drawer_group_social_item_fb)
					title = "Facebook";
				else if (childId == R.id.left_drawer_group_social_item_tb)
					title = "Tumblr";
				else if (childId == R.id.left_drawer_group_social_item_tw)
					title = "Twitter";
				else if (childId == R.id.left_drawer_group_social_item_yt)
					title = "Youtube";
				else
					title = "Instagram";
			}
			((TextView)convertView.findViewById(R.id.child_textview)).setText(title);
		}
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		if (getGroupId(groupPosition) == R.id.left_drawer_group_settings && !mShouldShowSettings)
			return false;
		return true;
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

