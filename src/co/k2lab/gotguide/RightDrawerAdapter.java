package co.k2lab.gotguide;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.k2lab.gotguide.model.Episode;
import co.k2lab.gotguide.model.Season;
import co.k2lab.gotguide.utils.Utils;

public class RightDrawerAdapter extends BaseExpandableListAdapter {
	private ArrayList<Season> seasons;
	private Context context;
	private int mCurrentGroupSelected = -1, mCurrentChildSelected = -1;

	public RightDrawerAdapter(Context context, ArrayList<Season> seasons) {
		this.context = context;
		if (seasons != null) {
			this.seasons = seasons;
		} else {
			this.seasons = new ArrayList<Season>();
		}
	}

	public void setCurrentSelected(int group, int child) {
		mCurrentGroupSelected = group;
		mCurrentChildSelected = child;
	}

	@Override
	public int getGroupCount() {
		return seasons.size() + 2;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition < getGroupCount() - 2) {
			return seasons.get(groupPosition).AiredEpisodesCount();
		} else {
			return 0;
		}

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
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.right_list_group, null);

		if (groupPosition < getGroupCount() - 2) {
			((TextView) convertView.findViewById(R.id.group_textview))
					.setText(seasons.get(groupPosition).getName());
			if (((Season) getGroup(groupPosition)).isExpanded()) {
				((ImageView) convertView.findViewById(R.id.group_indicator))
						.setImageResource(R.drawable.ic_action_collapse);
			} else {
				((ImageView) convertView.findViewById(R.id.group_indicator))
						.setImageResource(R.drawable.ic_action_expand);
			}
			convertView
					.setBackgroundResource(((Season) getGroup(groupPosition))
							.getBackgroundId());
		} else {
			convertView.setBackgroundResource(R.drawable.list_group_background);
			convertView.findViewById(R.id.group_seperator).setVisibility(View.INVISIBLE);
			((ImageView) convertView.findViewById(R.id.group_indicator))
					.setVisibility(View.INVISIBLE);
			TextView tv = (TextView) convertView
					.findViewById(R.id.group_textview);
			tv.setTypeface(null, Typeface.NORMAL);
			tv.setAllCaps(true);
			tv.setCompoundDrawablePadding(12);
			if (groupPosition == getGroupCount() - 1) {
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
				tv.setText(context.getResources().getString(R.string.feedback));
				tv.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.ic_drawer_mail, 0, 0, 0);
			} else {
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
				tv.setText(context.getResources().getString(R.string.remove_ads));
				tv.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.ic_drawer_beer, 0, 0, 0);
			}
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.right_list_item, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.item_textview);
		tv.setText(seasons.get(groupPosition).getEpisodes().get(childPosition)
				.getName());
		tv.setCompoundDrawablesWithIntrinsicBounds(seasons.get(groupPosition)
				.getEpisodes().get(childPosition).getIconId(), 0, 0, 0);
		
		
		if (((Episode)getChild(groupPosition, childPosition)).isNewEpisose()) {
			tv.setMaxWidth(Utils.convertDpToPixel(context, 250));
		}
		((View) convertView.findViewById(R.id.item_new_icon))
				.setVisibility(((Episode) getChild(groupPosition, childPosition))
						.isNewEpisose() ? View.VISIBLE : View.GONE);
		
		View v = (View) convertView.findViewById(R.id.item_selected_view);
		v.setVisibility(groupPosition == mCurrentGroupSelected
				&& childPosition == mCurrentChildSelected ? View.VISIBLE
				: View.INVISIBLE);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
