package liuhao.baway.com.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<ItemEntity> mList;
    private LayoutInflater mLayoutInflater;
    private BounceCircle mCircle;

    public ListViewAdapter(Context context, List<ItemEntity> list, BounceCircle circle) {
        mContext = context;
        mList = list;
        mCircle = circle;

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView name;
        public RoundNumber number;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ItemEntity entity = mList.get(position);

        if(convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (RoundNumber) convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(entity.getName());
        holder.number.setMessage(entity.getMessage());
        holder.number.setClickListener(new RoundNumber.ClickListener() {
            @Override
            public void onDown() {
                int[] position = new int[2];
                holder.number.getLocationOnScreen(position);

                int radius = holder.number.getWidth() / 2;

                mCircle.down(radius, position[0] + radius, position[1] - Util.getTopBarHeight((MainActivity) mContext) + radius, holder.number.getMessage());
                mCircle.setVisibility(View.VISIBLE); // 显示全屏范围的BounceCircle

                holder.number.setVisibility(View.INVISIBLE); // 隐藏固定范围的RoundNumber
                mCircle.setOrginView(holder.number);
            }

            @Override
            public void onMove(float curX, float curY) {
                mCircle.move(curX, curY);
            }

            @Override
            public void onUp() {
                mCircle.up();
            }
        });

        return convertView;
    }
}
