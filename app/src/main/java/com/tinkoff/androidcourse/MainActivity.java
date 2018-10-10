package com.tinkoff.androidcourse;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private List<Worker> workers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(this));

        workers = WorkerGenerator.generateWorkers(7);

        final WorkerRecyclerAdapter adapter = new WorkerRecyclerAdapter();
        adapter.setWorkers(workers);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new MyItemTouchCallback(adapter);

        ItemTouchHelper ith = new ItemTouchHelper(callback);
        ith.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Single.fromCallable(() -> {
            List<Worker> newWorkers = getNewWorkers(adapter.getWorkers());
            DiffUtil.DiffResult dr = DiffUtil.calculateDiff(new WorkerDiffCallback(adapter.getWorkers(), newWorkers), false);
            return new Pair<>(newWorkers, dr);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pair -> {
            adapter.setWorkers(pair.first);
            pair.second.dispatchUpdatesTo(adapter);
        }));
    }

    private List<Worker> getNewWorkers(List<Worker> oldWorkers) {
        List<Worker> newWorkers = new ArrayList<>();
        for (int i = 0; i < oldWorkers.size(); i++) {
            newWorkers.add(oldWorkers.get(i));
            if (i != 0 && (i % 3) == 0) {
                newWorkers.add(WorkerGenerator.generateWorker());
            }
        }
        return newWorkers;
    }

    interface ItemTouchHelperAdapter {
        void onItemMove(int from, int to);

        void onItemDismiss(int position);
    }

    class WorkerDiffCallback extends DiffUtil.Callback {

        private final List<Worker> oldWorkers;
        private final List<Worker> newWorkers;

        WorkerDiffCallback(List<Worker> oldWorkers, List<Worker> newWorkers) {
            this.oldWorkers = oldWorkers;
            this.newWorkers = newWorkers;
        }

        @Override
        public int getOldListSize() {
            return oldWorkers.size();
        }

        @Override
        public int getNewListSize() {
            return newWorkers.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldWorkers.get(oldItemPosition).getId() == newWorkers.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldWorkers.get(oldItemPosition).equals(newWorkers.get(newItemPosition));
        }
    }

    class MyItemTouchCallback extends ItemTouchHelper.Callback {

        private float COLOR_HUE = 0;
        private float COLOR_BRIGHTNESS = 1;

        private Paint paint = new Paint();

        private final ItemTouchHelperAdapter adapter;

        MyItemTouchCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlag, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder, RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            adapter.onItemMove(from, to);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View view = viewHolder.itemView;
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                int width = view.getRight() + params.rightMargin;
                int color;
                if (dX == 0) {
                    color = Color.rgb(255, 255, 255);
                } else {
                    color = Color.HSVToColor(new float[]{COLOR_HUE, Math.abs(dX) / width, COLOR_BRIGHTNESS});
                }

                paint.setColor(color);

                c.drawRect(view.getLeft() - params.leftMargin, view.getTop() - params.topMargin, view.getRight() + params.rightMargin, view.getBottom() + params.bottomMargin, paint);
            }


        }
    }

}
