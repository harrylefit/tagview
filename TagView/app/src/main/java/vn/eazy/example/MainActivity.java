package vn.eazy.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow.HorizontalPosition;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow.VerticalPosition;

import vn.eazy.tagview.core.ActiveHashTag;
import vn.eazy.tagview.list.ListViewPopup;
import vn.eazy.tagview.widget.TagEditTextView;
import vn.eazy.tagview.widget.TagTextView;

public class MainActivity extends AppCompatActivity implements ActiveHashTag.OnHashTagClickListener {
    private TagTextView tvContent;
    private TagEditTextView tvInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = (TagTextView) findViewById(R.id.tvContent);
        String content = "<b>Harry</b> #harry @long #jon <i>Long</i> #john @julian";
        tvContent.appendText(content);
        tvContent.setHashTagClickListener(this);

        tvInput = (TagEditTextView) findViewById(R.id.tvInput);
        tvInput.setHashTagClickListener(this);
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
        new ListViewPopup(tvInput.getContext()).showOnAnchor(tvInput, VerticalPosition.BELOW, HorizontalPosition.ALIGN_LEFT);
    }
}
