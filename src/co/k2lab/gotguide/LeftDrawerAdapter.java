package co.k2lab.gotguide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeftDrawerAdapter extends BaseExpandableListAdapter {
	
	private static final int GROUP_COUNT = 8;
	private static final int[] CHILD_COUNT = { 0, 0, 0, 0, 0, 2, 4, 5 };
	private Context context;
	// private Boolean[] isExpanded =
	// {false,false,false,false,false,false,false,false};
	private int mCurrentGroupSelected = -1, mCurrentChildSelected = -1;

	public LeftDrawerAdapter(Context context) {
		this.context = context;
	}

	/*
	 * @Override public void onGroupExpanded(int groupPosition) {
	 * isExpanded[groupPosition] = true; super.onGroupExpanded(groupPosition); }
	 * 
	 * @Override public void onGroupCollapsed(int groupPosition) {
	 * isExpanded[groupPosition] = false; super.onGroupCollapsed(groupPosition);
	 * }
	 */

	public void setCurrentSelected(int group, int child) {
		mCurrentGroupSelected = group;
		mCurrentChildSelected = child;
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
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.left_list_group, null);

		ListGroupItem item = new ListGroupItem(this.context, groupPosition);
		((TextView) convertView.findViewById(R.id.group_textview))
				.setText(item.title);

		if (groupPosition <= GROUP_COUNT - 4) { // Groups with no child
			convertView.findViewById(R.id.group_icon).setVisibility(View.VISIBLE);
			((ImageView) convertView.findViewById(R.id.group_icon))
					.setImageDrawable(item.iconDrawable);
		} else {
			((View) convertView.findViewById(R.id.group_seperator)).setVisibility(View.VISIBLE);
			ImageView indicator = ((ImageView) convertView
					.findViewById(R.id.group_indicator));
			indicator.setVisibility(View.VISIBLE);
			if (isExpanded) {
				indicator.setImageResource(R.drawable.ic_action_collapse);
			} else {
				indicator.setImageResource(R.drawable.ic_action_expand);
			}
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {		
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (groupPosition == 5) {// settings group
				if (childPosition == 0) {
					convertView = inflater.inflate(R.layout.left_list_language_item, null);	
				} else {
					convertView = inflater.inflate(R.layout.left_list_spoiler_item, null);
				}
			} else { // other groups
				ListChildItem childItem = new ListChildItem(context, groupPosition, childPosition);
				convertView = inflater.inflate(R.layout.left_list_item, null);
				TextView tv = (TextView) convertView.findViewById(R.id.item_textview);
				tv.setText(childItem.title);
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
	
	class ListGroupItem {
		public int titleId = -1;
		public int iconId = -1;
		public String title = "";
		public Drawable iconDrawable;
		
		public ListGroupItem(Context context, int groupPosition) {
			switch (groupPosition) {
			case 0:
				titleId = R.string.home;
				iconId = R.drawable.drawer_home;
				break;
			case 1:
				titleId = R.string.map;
				iconId = R.drawable.drawer_map;
				break;
			case 2:
				titleId = R.string.houses;
				iconId = R.drawable.drawer_houses;
				break;
			case 3:
				titleId = R.string.people;
				iconId = R.drawable.drawer_people;
				break;
			case 4:
				titleId = R.string.appendix;
				iconId = R.drawable.drawer_appendix;
				break;
			case 5:
				titleId = R.string.settings;
				break;
			case 6:
				titleId = R.string.HBO;
				break;
			case 7:
				titleId = R.string.social;
				break;
			default:
				break;
			}

			title = context.getResources().getString(titleId);
			if (iconId != -1) { 
				iconDrawable =	context.getResources().getDrawable(iconId);
			}
		}
	}
	
	class ListChildItem {
		private String title = "";
		private String link = "";
		public ListChildItem(Context context, int groupPosition, int childPosition) {
			if (groupPosition == 6) { // HBO section
				switch (childPosition) {
				case 0:
					title = "HBO.com";
					link = "";
					break;
				case 1:
					title = "HBO GO";
					link = "";
					break;
				case 2:
					title = "HBO Connect";
					link = "";
					break;
				case 3:
					title = "HBO Store";
					link = "";
					break;
				default:
					break;
				}			
			} else if (groupPosition == 7) { // SOCIAL section
				switch (childPosition) {
				case 0:
					title = "Facebook";
					link = "";
					break;
				case 1:
					title = "Tumblr";
					link = "";
					break;
				case 2:
					title = "Twitter";
					link = "";
					break;
				case 3:
					title = "Youtube";
					link = "";
					break;
				case 4:
					title = "Instagram";
					link = "";
				}
			}
		}
	}
}

