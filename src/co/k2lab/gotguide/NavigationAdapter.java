package co.k2lab.gotguide;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.k2lab.gotguide.model.Season;

public class NavigationAdapter extends BaseExpandableListAdapter {
	private ArrayList<Season> seasons;
	private Context context;
	
	public NavigationAdapter(Context context, ArrayList<Season> seasons) {
		this.context = context;
		if (seasons != null) {
			this.seasons = seasons;
		} else {
			this.seasons = new ArrayList<Season>();
		}
	}

	@Override
	public int getGroupCount() {		
		return seasons.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return seasons.get(groupPosition).AiredEpisodesCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return seasons.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return seasons.get(groupPosition).getEpisodes().get(childPosition);
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
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_group, null);
			convertView.setBackgroundResource(((Season)getGroup(groupPosition)).getBackgroundId());
		}
		((TextView) convertView.findViewById(R.id.group_textview)).setText(seasons.get(groupPosition).getName());
		if (((Season)getGroup(groupPosition)).isExpanded()) {
			((ImageView) convertView.findViewById(R.id.group_indicator)).setImageResource(R.drawable.ic_action_expand);
		} else {
			((ImageView) convertView.findViewById(R.id.group_indicator)).setImageResource(R.drawable.ic_action_collapse);
		}
		return convertView;
	}
	
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.item_textview);
		tv.setText(seasons.get(groupPosition).getEpisodes().get(childPosition).getName());
		tv.setCompoundDrawablesWithIntrinsicBounds(seasons.get(groupPosition).getEpisodes().get(childPosition).getIconId(), 0,0,0);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
