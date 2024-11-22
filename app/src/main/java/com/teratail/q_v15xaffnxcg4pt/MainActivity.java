package com.teratail.q_v15xaffnxcg4pt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.time.LocalTime;
import java.util.*;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ListView list = findViewById(R.id.list);
    Adapter adapter = new Adapter();
    list.setAdapter(adapter);

    adapter.add(new Item(LocalTime.of(9,0), LocalTime.of(12,0)));
    adapter.add(new Item(LocalTime.of(13,30), LocalTime.of(19,0)));
    adapter.add(new Item(LocalTime.of(8,0), LocalTime.of(9,30)));
    adapter.add(new Item(LocalTime.of(6,0), LocalTime.of(8,0)));
  }

  private static class Item {
    final LocalTime start, endExclusive;
    Item(LocalTime start, LocalTime endExclusive) {
      this.start = start;
      this.endExclusive = endExclusive;
    }
  }

  private static class Adapter extends BaseAdapter {
    private List<Item> itemList = new ArrayList<>();

    void add(Item item) {
      itemList.add(item);
      notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
      return itemList.size();
    }

    @Override
    public Object getItem(int position) {
      return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder vh = convertView == null ? new ViewHolder(parent) : (ViewHolder)convertView.getTag();
      return vh.bind(itemList.get(position));
    }

    private static class ViewHolder {
      private final View itemView;
      private final TimetableView timetableView;
      ViewHolder(ViewGroup parent) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        timetableView = itemView.findViewById(R.id.timetable1);
        timetableView.setLabelSupplier(i -> {
          if(i%2 != 0) return null;
          int j = i/2+8;
          return j%3==0 ? ""+j : null;
        });
      }
      View bind(Item item) {
        int start = getIndex(item.start);
        int endExclusive = getIndex(item.endExclusive);
        if(endExclusive == 0) endExclusive = 24*2;
        Log.d("MainActivity", "start="+start+", endExclusive="+endExclusive);
        timetableView.clear();
        for(int i=start; i<endExclusive; i++) {
          timetableView.setReserveState(i, true);
        }
        return itemView;
      }
      private int getIndex(LocalTime time) {
        int hour = time.getHour();
        if(hour < 8) hour += 24;
        return (hour-8)*2 + (time.getMinute()==0?0:1);
      }
    }
  }
}