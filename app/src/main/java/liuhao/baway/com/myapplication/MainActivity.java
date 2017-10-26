package liuhao.baway.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RoundNumber unreadMessage;
    private BounceCircle circle;
    private ListView list;
    private ListViewAdapter adapter;
    public static boolean isTouchable = true; // 是否响应按键事件，如果一个气泡已经在响应，其它气泡就不响应，同一界面始终最多只有一个气泡响应按键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.okok);

        circle = (BounceCircle) findViewById(R.id.circle);
        initList();

        unreadMessage = (RoundNumber) findViewById(R.id.unread_message);
        unreadMessage.setMessage("3");
        unreadMessage.setClickListener(new RoundNumber.ClickListener() {
            @Override
            public void onDown() {
                int[] position = new int[2];
                unreadMessage.getLocationOnScreen(position);

                int radius = unreadMessage.getWidth() / 2;
                circle.down(radius, position[0] + radius, position[1] - Util.getTopBarHeight(MainActivity.this) + radius, unreadMessage.getMessage());
                circle.setVisibility(View.VISIBLE); // 显示全屏范围的BounceCircle

                unreadMessage.setVisibility(View.INVISIBLE); // 隐藏固定范围的RoundNumber
                circle.setOrginView(unreadMessage);
            }

            @Override
            public void onMove(float curX, float curY) {
                circle.move(curX, curY);
            }

            @Override
            public void onUp() {
                circle.up();
            }
        });

        circle.setFinishListener(new BounceCircle.FinishListener() {
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "disappear", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 初始化列表
     */
    private void initList() {
        list = (ListView) findViewById(R.id.list);
        String names[] = {"张三", "李四", "王五", "小明", "老师", "钱六", "小K", "大白", "佐助", "小丸子"};
        List<ItemEntity> result = new ArrayList<ItemEntity>();

        for (int i = 0; i < names.length; i++) {
            ItemEntity entity = new ItemEntity();
            entity.setName(names[i]);
            entity.setMessage("" + (int) (Math.random() * 10));
            result.add(entity);
        }

        adapter = new ListViewAdapter(this, result, circle);
        list.setAdapter(adapter);

    }
}
