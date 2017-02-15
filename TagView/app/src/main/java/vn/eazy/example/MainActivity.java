package vn.eazy.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.eazy.tagview.core.ActiveHashTag;
import vn.eazy.tagview.widget.TagEditTextView;

public class MainActivity extends AppCompatActivity implements ActiveHashTag.OnHashTagClickListener, TagEditTextView.OnTypingListener {
//    private TagTextView tvContent;
    private TagEditTextView tvInput;

    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tvContent = (TagTextView) findViewById(R.id.tvContent);
//        String content = "<b>Harry</b> #harry @long #jon <i>Long</i> #john @julian";
//        tvContent.appendText(content);
//        tvContent.setHashTagClickListener(this);

        tvInput = (TagEditTextView) findViewById(R.id.tvInput);
        tvInput.setHashTagClickListener(this);
        tvInput.setOnTypingListener(this);

        //Todo dummny data to test
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User("user " + i, "avatar Url", "Desc");
            users.add(user);
        }

        User[] dt = new User[users.size()];
        for (int i = 0; i < users.size(); i++) {
            dt[i] = users.get(i);
        }

        List<String> strings = new ArrayList<>();
        for (User user : users) {
            strings.add(user.getItemTitle());
        }

    }


    @Override
    public void onHashTagClicked(String hashTag) {
        Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTypingHashTag(String hashTag) {
        tvInput.showSuggestionDataPopup();
        Log.i("HashTag",hashTag);
    }

    @Override
    public void onTypingMention(String mention) {
        tvInput.showSuggestionDataPopup();
        Log.i("Mention",mention);
    }
}
