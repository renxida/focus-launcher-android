package co.siempo.phone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.AppAssignmentActivity;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.ToolPositioningActivity;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by Shahab on 2/23/2017.
 */

public class ToolsMenuAdapter extends RecyclerView.Adapter<ToolsMenuAdapter.ViewHolder> {

    private final Context context;
    private List<MainListItem> mainListItemList;
    private boolean isHideIconBranding = true, isBottomDoc = false;
    private HashMap<Integer, AppMenu> map;

    public ToolsMenuAdapter(Context context, boolean isHideIconBranding, boolean isBottomDoc, List<MainListItem> mainListItemList) {
        this.context = context;
        this.mainListItemList = mainListItemList;
        this.isHideIconBranding = isHideIconBranding;
        this.isBottomDoc = isBottomDoc;
        map = CoreApplication.getInstance().getToolsSettings();
    }

    @Override
    public ToolsMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_item_grid, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MainListItem item = mainListItemList.get(position);
        final AppMenu appMenu = map.get(item.getId());
        if (appMenu.isVisible() && item.getId() != 12) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(item.getTitle())) {
                holder.text.setText(item.getTitle());
            }
            if (isHideIconBranding) {
                holder.icon.setImageResource(item.getDrawable());
            } else {
                holder.text.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(appMenu.getApplicationName()));
                Bitmap bitmap = CoreApplication.getInstance().getBitmapFromMemCache(appMenu.getApplicationName());
                if (bitmap != null) {
                    holder.icon.setImageBitmap(bitmap);
                } else {
                    BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(context, appMenu.getApplicationName());
                    CoreApplication.getInstance().includeTaskPool(bitmapWorkerTask);
                    holder.icon.setImageResource(item.getDrawable());
                }
            }
        } else {
            holder.linearLayout.setVisibility(View.INVISIBLE);
        }

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, ToolPositioningActivity.class);
                intent.putExtra("ID", mainListItemList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
                ((CoreActivity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = item.getId();
                if (holder.linearLayout.getVisibility() == View.VISIBLE && id != 12) {
                    if (!appMenu.getApplicationName().equalsIgnoreCase("")) {
                        if (appMenu.getApplicationName().equalsIgnoreCase("Notes")) {
                            new ActivityHelper(context).openNotesApp(false);
                        } else {
                            if (UIUtils.isAppInstalled(context, appMenu.getApplicationName().trim())) {
                                if (PrefSiempo.getInstance(context).read(PrefSiempo.JUNKFOOD_APPS,
                                        new HashSet<String>()).contains(appMenu.getApplicationName().trim())) {
                                    openAppAssignmentScreen(item);
                                } else {
//                                if a 3rd party app is already assigned to this tool
                                    new ActivityHelper(context).openAppWithPackageName(appMenu.getApplicationName().trim());
                                }
                            }
                        }
                    } else {
                        if (CoreApplication.getInstance().getApplicationByCategory(id).size() == 0) {
                            openAppAssignmentScreen(item);
                        } else if (CoreApplication.getInstance().getApplicationByCategory(id).size() == 1
                                && !PrefSiempo.getInstance(context).read(PrefSiempo.JUNKFOOD_APPS,
                                new HashSet<String>()).contains(appMenu.getApplicationName().trim())) {
//                                if a 3rd party app is already assigned to this tool
                            String strPackageName = CoreApplication.getInstance().getApplicationByCategory(id).get(0).activityInfo.packageName;
                            new ActivityHelper(context).openAppWithPackageName(strPackageName);
                        } else {
                            openAppAssignmentScreen(item);
                        }
                    }
                }
            }
        });
    }

    /**
     * if the user has multiple apps that are installed and relevant to this tool (e.g. tool is browser, and Chrome and Firefox are installed)
     * navigate to the tool-app assignment screen
     * if the user has one one app that is installed and relevant to this tool (e.g. tool is browser, and only Chrome is installed)
     * if that 3rd party app IS currently flagged by this user as junkfood
     * navigate to the tool-app assignment screen
     *
     * @param item
     */
    private void openAppAssignmentScreen(MainListItem item) {
        Intent intent = new Intent(context, AppAssignmentActivity.class);
        intent.putExtra(Constants.INTENT_MAINLISTITEM, item);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return isBottomDoc ? 4 : 12;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layout;
        // each data item is just a string in this case
        ImageView icon, imgView;
        TextView text;
        TextView textDefaultApp;
        RelativeLayout relMenu;
        private LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            linearLayout = v.findViewById(R.id.linearList);
            relMenu = v.findViewById(R.id.relMenu);
            text = v.findViewById(R.id.text);
            textDefaultApp = v.findViewById(R.id.textDefaultApp);
            icon = v.findViewById(R.id.icon);
            imgView = v.findViewById(R.id.imgView);
        }
    }
}
