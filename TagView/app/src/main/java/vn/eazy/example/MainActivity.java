package vn.eazy.example;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import vn.eazy.tagview.adapter.EazySuggestionAdapter;
import vn.eazy.tagview.core.ActiveHashTag;
import vn.eazy.tagview.event.SuggestionItemEvent;
import vn.eazy.tagview.model.SimpleData;
import vn.eazy.tagview.widget.TagEditTextView;
import vn.eazy.tagview.widget.TagTextView;

public class MainActivity extends AppCompatActivity implements ActiveHashTag.OnHashTagClickListener, TagEditTextView.OnTypingListener {
    private TagTextView tvContent;
    private TagEditTextView<User> tvInput;
    private TagEditTextView<User> tvInput2;
    private List<User> users;
    private EazySuggestionAdapter eazySuggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = (TagTextView) findViewById(R.id.tvContent);
        String content = "<b>Harry</b> #harry-aa @long #heavy_enginneering <i>Long</i> #john @julian";
        tvContent.appendText(content);
        tvContent.setHashTagClickListener(this);
        tvInput = (TagEditTextView) findViewById(R.id.tvInput);
        tvInput.setHashTagClickListener(this);
        tvInput.setOnTypingListener(this);

        tvInput2 = (TagEditTextView) findViewById(R.id.tvInput2);
        tvInput2.setHashTagClickListener(this);
        tvInput2.setOnTypingListener(this);

        eazySuggestionAdapter = new EazySuggestionAdapter(getApplicationContext(),dpToPx(60,getApplicationContext()));

        List<SimpleData> simpleDatas = new ArrayList<>();
        simpleDatas.add(new SimpleData("Harry", "Harry"));
        simpleDatas.add(new SimpleData("Henry","Henry"));
        simpleDatas.add(new SimpleData("John", "Harry"));
        simpleDatas.add(new SimpleData("Brian", "Harry"));
        simpleDatas.add(new SimpleData("Ben", "Harry"));

        eazySuggestionAdapter.addAll(simpleDatas);

        tvInput.setSuggestionAdapter(eazySuggestionAdapter);

        tvInput2.setSuggestionAdapter(eazySuggestionAdapter);


//        simpleDatas.add(new SimpleData("Ray", "SS"));
//        simpleDatas.add(new SimpleData("Liam", "SS"));
//        simpleDatas.add(new SimpleData("Louis", "SS"));
//        simpleDatas.add(new SimpleData("Ben", "SS"));
//        simpleDatas.add(new SimpleData("Ray", "SS"));
//        simpleDatas.add(new SimpleData("Liam", "SS"));
//        simpleDatas.add(new SimpleData("Louis", "SS"));

        eazySuggestionAdapter.addAll(simpleDatas);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    @Subscribe
    public void onEvent(SuggestionItemEvent suggestionItemEvent) {
        Toast.makeText(getApplicationContext(), suggestionItemEvent.getData().getItemTitle()
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        tvInput.release();
        tvInput2.release();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMentionClicked(String mention) {
        Toast.makeText(getApplicationContext(), "Mention: " + mention, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTypingHashTag(String hashTag) {
        tvInput.showSuggestionDataPopup();
        Log.i("HashTag", hashTag);
    }

    @Override
    public void onTypingMention(String mention) {
        tvInput2.showSuggestionDataPopup(true);
        Log.i("Mention", mention);
    }
}
