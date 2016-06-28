package minium.co.launcher2.ui;


import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.filter.FilterAdapter;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.model.MainListItem;
import minium.co.launcher2.model.OptionsListItem;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_filter_2)
public class ContextualOptionFragment extends CoreFragment {

    @ViewById
    ListView listView;

    @Pref
    DroidPrefs_ prefs;

    @Bean
    ActionItemManager manager;

    FilterAdapter adapter;

    List<MainListItem> items;

    private String mSearchString = null;


    public ContextualOptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Background
    void loadData() {
        items = new ArrayList<>();
        loadOptions();
        loadView();
    }

    @UiThread
    void loadView() {
        adapter = new FilterAdapter(getActivity(), items);
        listView.setAdapter(adapter);
    }

    private void loadOptions() {
        items.clear();

        if (manager.has(ActionItem.ActionItemType.TEXT))
            items.add(new MainListItem(new OptionsListItem(0, "{fa-paper-plane}", "Send")));
        else if (manager.getCurrent().getType() == ActionItem.ActionItemType.CONTACT_NUMBER) {
            items.add(new MainListItem(new OptionsListItem(1, "{fa-phone}", "Call")));
            items.add(new MainListItem(new OptionsListItem(2, "{fa-comment-o}", "Text")));
            items.add(new MainListItem(new OptionsListItem(3, "{fa-user}", "View Contact")));
        }
        else if (manager.getCurrent().getType() == ActionItem.ActionItemType.DATA) {
            if (manager.has(ActionItem.ActionItemType.CONTACT_NUMBER)) {
                items.add(new MainListItem(new OptionsListItem(0, "{fa-paper-plane}", "Send")));
            } else {
                items.add(new MainListItem(new OptionsListItem(4, "{fa-pencil}", "Save Note")));
                items.add(new MainListItem(new OptionsListItem(5, "{fa-user-plus}", "Create Contact")));
            }
        }

        if (adapter != null) adapter.notifyDataSetChanged();

    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        position = adapter.getItem(position).getOptionsListItem().getPosition();

        switch (position) {
            case 0:
                // TODO: progress bar
                manager.getCurrent().setCompleted(true);
                if (!manager.has(ActionItem.ActionItemType.TEXT)) {
                    manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                }
                manager.add(new ActionItem(ActionItem.ActionItemType.END_OP));

                manager.fireEvent();
                break;
            case 1:
                manager.add(new ActionItem(ActionItem.ActionItemType.CALL));
                manager.fireEvent();
                break;
            case 2:
                manager.add(new ActionItem(ActionItem.ActionItemType.TEXT));
                manager.fireEvent();
                loadOptions();
                break;
            default:
                UIUtils.alert(getActivity(), getString(R.string.msg_not_yet_implemented));
                break;
        }

    }
}
