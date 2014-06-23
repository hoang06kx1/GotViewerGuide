package co.k2lab.gotguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LeftDrawerAdapter extends BaseExpandableListAdapter {
	
	private static final int GROUP_COUNT = 4;
	private static final int[] CHILD_COUNT = {5, 2, 4, 5 };
	private static final int[] GROUP_STRINGS_ID = {R.string.in_this_episode, R.string.settings, R.string.HBO, R.string.social};
	private static final int[] CHILD_ICON_ID = {R.drawable.drawer_home, R.drawable.drawer_map, R.drawable.drawer_houses, R.drawable.drawer_people, R.drawable.drawer_appendix}; 
	private static final int[] CHILD_STRING_ID = {R.string.home, R.string.map, R.string.houses, R.string.people, R.string.appendix, R.string.language, R.string.spoiler_alerts};
	private static final String[] CHILD_STRINGS = {"HBO.com", "HBO GO", "HBO Connect", "HBO Store", "Facebook", "Tumblr", "Twitter", "Youtube", "Instagram"};
	
	private MainActivity mMainActivity;

	public LeftDrawerAdapter(MainActivity activity) {
		this.mMainActivity = activity;
	}

	@Override
	public int getGroupCount() {
		return GROUP_COUNT;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return CHILD_COUNT[groupPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
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
		((TextView) convertView.findViewById(R.id.group_textview))
				.setText(mMainActivity.getResources().getString(GROUP_STRINGS_ID[groupPosition]));		
		// hide settings if not ready 
		if ((groupPosition == 1 && !mMainActivity.isSettingsReady() && convertView != null) || (groupPosition == 1)) { // TODO: need to be fixed for ver 1.2
			View view = new View(mMainActivity);
			view.setLayoutParams(new LinearLayout.LayoutParams(0,0));
			return view;
		}
		
		if (groupPosition > 0) {
			ImageView indicator = ((ImageView) convertView.findViewById(R.id.group_indicator));
			indicator.setVisibility(View.VISIBLE);
			if (isExpanded) {
				indicator.setImageResource(R.drawable.drawer_top_arrow);
			} else {
				indicator.setImageResource(R.drawable.drawer_bottom_arrow);
			}
		}
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {		
			LayoutInflater inflater = (LayoutInflater) this.mMainActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (groupPosition == 0) {
				convertView = inflater.inflate(R.layout.left_list_item, null);
				ImageView icon = (ImageView) convertView.findViewById(R.id.child_icon);
				icon.setVisibility(View.VISIBLE);
				icon.setImageResource(CHILD_ICON_ID[childPosition]);
				((TextView)convertView.findViewById(R.id.child_textview)).setText(mMainActivity.getResources().getString(CHILD_STRING_ID[childPosition]));
			
			} else if (groupPosition == 1) {// settings group
				convertView  = inflater.inflate(R.layout.left_list_settings_item, null);
				if (childPosition == 0) {
					((TextView)convertView.findViewById(R.id.textview)).setText(mMainActivity.getResources().getString(R.string.language));
				} else {
					((TextView)convertView.findViewById(R.id.textview)).setText(mMainActivity.getResources().getString(R.string.spoiler_alerts));
				}
				((TextView)convertView.findViewById(R.id.textview_change)).setText(mMainActivity.getResources().getString(R.string.change));
				
			} else if (groupPosition > 1) { // HBO & SOCIAL 
				convertView = inflater.inflate(R.layout.left_list_item, null);
				if (groupPosition == 2) {
					((TextView)convertView.findViewById(R.id.child_textview)).setText(CHILD_STRINGS[childPosition]);
			
				} else {
					((TextView)convertView.findViewById(R.id.child_textview)).setText(CHILD_STRINGS[childPosition+4]);
				}
				
				convertView.findViewById(R.id.child_link_icon).setVisibility(View.VISIBLE);
			}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
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

