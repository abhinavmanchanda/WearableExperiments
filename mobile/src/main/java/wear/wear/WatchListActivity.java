package wear.wear;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;


/**
 * An activity representing a list of Watches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WatchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WatchListFragment} and the item details
 * (if present) is a {@link WatchDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link WatchListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class WatchListActivity extends Activity
    implements WatchListFragment.Callbacks {

  public static final String INDEX = "index";
  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;
  private com.google.android.gms.common.api.GoogleApiClient mGoogleApiClient;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_watch_list);

    if (findViewById(R.id.watch_detail_container) != null) {
      // The detail container view will be present only in the
      // large-screen layouts (res/values-large and
      // res/values-sw600dp). If this view is present, then the
      // activity should be in two-pane mode.
      mTwoPane = true;

      // In two-pane mode, list items should be given the
      // 'activated' state when touched.
      ((WatchListFragment) getFragmentManager()
          .findFragmentById(R.id.watch_list))
          .setActivateOnItemClick(true);
    }

    int notificationId = 001;
// Build intent for notification content
    Intent viewIntent = new Intent(this, WatchDetailActivity.class);
    viewIntent.putExtra(WatchDetailFragment.ARG_ITEM_ID, 1);
    PendingIntent viewPendingIntent =
        PendingIntent.getActivity(this, 0, viewIntent, 0);

    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
    Uri geoUri = Uri.parse("http://www.google.com");
    mapIntent.setData(geoUri);
    PendingIntent mapPendingIntent =
        PendingIntent.getActivity(this, 0, mapIntent, 0);



    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(Wearable.API)
        .build();
        mGoogleApiClient.connect();
  }

  /**
   * Callback method from {@link WatchListFragment.Callbacks}
   * indicating that the item with the given ID was selected.
   */
  @Override
  public void onItemSelected(String position) {
    updateWearableNotification(position);
    if (mTwoPane) {
      // In two-pane mode, show the detail view in this activity by
      // adding or replacing the detail fragment using a
      // fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putString(WatchDetailFragment.ARG_ITEM_ID, position);
      WatchDetailFragment fragment = new WatchDetailFragment();
      fragment.setArguments(arguments);
      getFragmentManager().beginTransaction()
          .replace(R.id.watch_detail_container, fragment)
          .commit();

    } else {
      // In single-pane mode, simply start the detail activity
      // for the selected item ID.
      Intent detailIntent = new Intent(this, WatchDetailActivity.class);
      detailIntent.putExtra(WatchDetailFragment.ARG_ITEM_ID, position);
      startActivity(detailIntent);
    }
  }

  private void updateWearableNotification(String id) {
    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/index");

    DataMap map = putDataMapRequest.getDataMap();
    map.putString(INDEX, id);

    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest());
  }
}
