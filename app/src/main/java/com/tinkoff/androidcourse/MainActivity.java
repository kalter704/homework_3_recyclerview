package com.tinkoff.androidcourse;

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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private List<Worker> workers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(this, R.dimen.card_insets));

        workers = WorkerGenerator.generateWorkers(7);

        final WorkerRecyclerAdapter adapter = new WorkerRecyclerAdapter();
        adapter.setWorkers(workers);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlag, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(adapter.getWorkers(), from, to);
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int removedPosition = viewHolder.getAdapterPosition();
                adapter.getWorkers().remove(removedPosition);
                adapter.notifyItemRemoved(removedPosition);
            }
        };

        ItemTouchHelper ith = new ItemTouchHelper(callback);
        ith.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Single.fromCallable(new Callable<Pair<List<Worker>, DiffUtil.DiffResult>>() {
                    @Override
                    public Pair<List<Worker>, DiffUtil.DiffResult> call() {

                        List<Worker> newWorkers = getNewWorkers(adapter.getWorkers());
                        DiffUtil.DiffResult dr = DiffUtil.calculateDiff(new WorkerDiffCallback(adapter.getWorkers(), newWorkers), false);
                        return new Pair<>(newWorkers, dr);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<List<Worker>, DiffUtil.DiffResult>>() {
                    @Override
                    public void accept(Pair<List<Worker>, DiffUtil.DiffResult> pair) {
                        adapter.setWorkers(pair.first);
                        pair.second.dispatchUpdatesTo(adapter);
                    }
                });

            }
        });
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
}
