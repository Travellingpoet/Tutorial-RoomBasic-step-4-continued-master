package com.example.roombasic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WordListFragment extends Fragment {
    WordViewModel wordViewModel;
    RecyclerView recyclerView;
    MyAdapter myAdapter1, myAdapter2;
    FloatingActionButton floatingActionButton;
    private LiveData<List<Word>> filteredWords;
    private static final String ADAPTER_STYLE_KEY = "adapter_style_key";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Word> allWords;

    public WordListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filteredWords.removeObservers(requireActivity());
                String pattern = s.trim();
                filteredWords = wordViewModel.getFilteredWordsLive(pattern);
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();
                        allWords = words;
                        if (temp != words.size()) {
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("确定要清除吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wordViewModel.deleteAllWords();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.item_card_style:
                SharedPreferences shp = requireActivity().getSharedPreferences(ADAPTER_STYLE_KEY, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = shp.edit();
                if (viewType) {
                    recyclerView.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {
                    recyclerView.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }


    void initView() {
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        recyclerView = requireActivity().findViewById(R.id.word_recycle);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
                super.onAnimationFinished(viewHolder);
            }
        });
//        recyclerView.setAdapter(myAdapter1);
        SharedPreferences shp = requireActivity().getSharedPreferences(ADAPTER_STYLE_KEY, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
        SharedPreferences.Editor editor = shp.edit();
        if (viewType) {
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
        }
        editor.apply();
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                allWords = words;
                if (temp != words.size()) {
                    recyclerView.smoothScrollBy(0, -200);
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }

            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_wordListFragment2_to_wordAddFragment);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Word wordToDelete = allWords.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(wordToDelete);
            }
        }).attachToRecyclerView(recyclerView);
    }

}