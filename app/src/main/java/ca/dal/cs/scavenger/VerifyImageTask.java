package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class VerifyImageTask extends AppCompatActivity implements
        OnTaskMarkedVerifiedListener {

    private int mTaskIndex;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_image_task);

        // Load the task
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        mTaskIndex = bundle.getInt("taskIndex", -1);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        ImageView image = (ImageView) findViewById(R.id.image);
        // This class loads the image from the URL
        // For video, you'll have to figure out how to download the file and play it
        // There may be a library to help with this, but I haven't looked
        // the URL is stored in mTask.dataURL
        LoadVisual
                .withContext(this)
                .fromSource(mTask)
                .into(image);


        // Setup the accept and deny button icons
        ImageButton accept = (ImageButton) findViewById(R.id.acceptButton);
        accept.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.GREEN)
        );

        ImageButton deny = (ImageButton) findViewById(R.id.denyButton);
        deny.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.RED)
        );
    }

    // Mark the task as verified if the author accepts it
    public void accept(View view) {
        mTask.is_verified = true;
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.markTaskVerified(mTask, this);
    }

    // Leave task as-is otherwise
    public void deny(View view) {
        finishView();
    }

    public void finishView() {
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onTaskMarkedVerified() {
       finishView();
    }

    @Override
    public void onError(String error) {
        Log.e("VerifyImageTask", error);
    }
}
