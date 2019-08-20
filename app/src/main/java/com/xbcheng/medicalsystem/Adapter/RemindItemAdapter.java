package com.xbcheng.medicalsystem.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.xbcheng.medicalsystem.R;
import com.xbcheng.medicalsystem.bean.RemindItem;
import com.xbcheng.medicalsystem.bean.SubRemindItem;

import java.util.List;

public class RemindItemAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<RemindItem> remindItems;

    public RemindItemAdapter(Context mContext, List<RemindItem> remindItems){
        this.mContext = mContext;
        this.remindItems = remindItems;
    }

    @Override
    public int getGroupCount() {
        return remindItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(getGroupCount()==0)
            return 0;

        return remindItems.get(groupPosition).getSubRemindItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return remindItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(getGroupCount()==0)
            return null;
        return remindItems.get(groupPosition).getSubRemindItems().get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder;
        if(convertView==null){
            convertView = View.inflate(mContext, R.layout.item_medicine_remind,null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);

        }else{
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.tvDate.setText(remindItems.get(groupPosition).getFormatDate());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ChildViewHolder childViewHolder;
        if(convertView==null){
            convertView = View.inflate(mContext,R.layout.sub_item_medicine_remind,null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        }else{
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final SubRemindItem subRemindItem = remindItems.get(groupPosition).getSubRemindItems().get(childPosition);

        childViewHolder.aSwitch.setChecked(subRemindItem.isTake());
        childViewHolder.tvName.setText(subRemindItem.getName());
        childViewHolder.tvDose.setText(subRemindItem.getDose());

        childViewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                subRemindItem.setTake(isChecked);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder{
        private TextView tvDate;

        public GroupViewHolder(View convertView ){
            this.tvDate = convertView.findViewById(R.id.tv_group_date);
        }
    }

    @Override
    public boolean isEmpty(){
        if(getGroupCount()==0){
            return true;
        }

        return false;
    }

    static class ChildViewHolder{
        Switch aSwitch;
        TextView tvName;
        TextView tvDose;

        public  ChildViewHolder(View convertView){
            this.aSwitch = convertView.findViewById(R.id.switch_remind);
            this.tvName = convertView.findViewById(R.id.tv_medicine_name);
            this.tvDose = convertView.findViewById(R.id.tv_medicine_dose);
        }
    }
}
