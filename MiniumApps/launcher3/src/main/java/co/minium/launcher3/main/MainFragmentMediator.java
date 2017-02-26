package co.minium.launcher3.main;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.BuildConfig;
import co.minium.launcher3.R;
import co.minium.launcher3.contact.ContactsLoader;
import co.minium.launcher3.contact.PhoneNumbersAdapter;
import co.minium.launcher3.event.CreateNoteEvent;
import co.minium.launcher3.helper.ActivityHelper;
import co.minium.launcher3.model.ContactListItem;
import co.minium.launcher3.model.MainListItem;
import co.minium.launcher3.model.MainListItemType;
import co.minium.launcher3.token.TokenItem;
import co.minium.launcher3.token.TokenItemType;
import co.minium.launcher3.token.TokenRouter;
import co.minium.launcher3.ui.PauseActivity_;
import de.greenrobot.event.EventBus;
import minium.co.core.util.UIUtils;

import static co.minium.launcher3.R.string.title_defaultLauncher;

/**
 * Created by shahab on 2/16/17.
 */

class MainFragmentMediator {

    private MainFragment fragment;

    private List<MainListItem> items;
    private List<ContactListItem> contactItems;

    MainFragmentMediator(MainFragment mainFragment) {
        this.fragment = mainFragment;
    }

    void loadData() {
        items = new ArrayList<>();
        loadActions();
        loadContacts();
        loadDefaults();
    }

    void resetData() {
        items.clear();
        loadActions();
        loadContacts();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }

    private void loadActions() {
        items.add(new MainListItem(4, fragment.getString(R.string.title_messages), "fa-users"));
        items.add(new MainListItem(5, fragment.getString(R.string.title_callLog), "fa-phone"));
        items.add(new MainListItem(6, fragment.getString(R.string.title_contacts), "fa-user"));
        items.add(new MainListItem(7, fragment.getString(R.string.title_pause), "fa-ban"));
        items.add(new MainListItem(8, fragment.getString(R.string.title_voicemail), "fa-microphone"));
        items.add(new MainListItem(9, fragment.getString(R.string.title_notes), "fa-sticky-note"));
        items.add(new MainListItem(10, fragment.getString(R.string.title_clock), "fa-clock-o"));
        items.add(new MainListItem(11, fragment.getString(R.string.title_settings), "fa-cogs"));
        items.add(new MainListItem(12, fragment.getString(R.string.title_theme), "fa-tint"));
        items.add(new MainListItem(13, fragment.getString(R.string.title_notificationScheduler), "fa-bell"));
        items.add(new MainListItem(14, fragment.getString(R.string.title_map), "fa-street-view"));

        if (!Build.MODEL.toLowerCase().contains("siempo")) {
            items.add(new MainListItem(15, fragment.getString(title_defaultLauncher), "fa-street-view"));
        }

        items.add(new MainListItem(16, fragment.getString(R.string.title_version, BuildConfig.VERSION_NAME), "fa-info-circle"));
    }

    private void loadContacts() {
        if (contactItems == null) {
            contactItems = new ContactsLoader().loadContacts(fragment.getActivity());
        }
        items.addAll(contactItems);
    }

    private void loadDefaults() {
        items.add(new MainListItem(1, fragment.getString(R.string.title_sendAsSMS), R.drawable.icon_sms, MainListItemType.DEFAULT));
        items.add(new MainListItem(2, fragment.getString(R.string.title_saveNote), R.drawable.icon_save_note, MainListItemType.DEFAULT));
        items.add(new MainListItem(3, fragment.getString(R.string.title_createContact), R.drawable.icon_create_user, MainListItemType.DEFAULT));
    }

    List<MainListItem> getItems() {
        return items;
    }

    private MainListAdapter getAdapter() {
        return fragment.getAdapter();
    }

    void listItemClicked(TokenRouter router, int position) {
        MainListItemType type = getAdapter().getItem(position).getItemType();

        switch (type) {

            case CONTACT:
                router.contactPicked((ContactListItem) getAdapter().getItem(position));
                break;
            case ACTION:
                position = getAdapter().getItem(position).getId();
                switch (position) {
                    case 1:
                    case 2:
                    case 3:
                    case 4: new ActivityHelper(fragment.getActivity()).openMessagingApp(); break;
                    case 5: break;
                    case 6: break;
                    case 7:
                        PauseActivity_.intent(fragment.getActivity()).start(); break;
                    case 8: break;
                    case 9: new ActivityHelper(fragment.getActivity()).openNotesApp(); break;
                    case 10: break;
                    case 11: break;
                }
                break;
            case DEFAULT:
                position = getAdapter().getItem(position).getId();

                switch (position) {
                    case 1:
                        router.sendText(fragment.getActivity());
                        break;
                    case 2:
                        router.createNote(fragment.getActivity());
                        EventBus.getDefault().post(new CreateNoteEvent());
                        break;
                    case 3:
                        router.createContact(fragment.getActivity());
                        break;
                    default:
                        UIUtils.alert(fragment.getActivity(), fragment.getString(R.string.msg_not_yet_implemented));
                        break;
                }
                break;
        }
    }

    public void contactPicker() {
        items.clear();
        loadContacts();
        loadDefaults();
        getAdapter().loadData(items);
        getAdapter().notifyDataSetChanged();
    }

    public void contactNumberPicker(int selectedContactId) {
        Uri phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.LABEL };
        String selection 		= ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] 	selectionArgs 	= new String[] { Long.toString(selectedContactId) };

        Cursor mCursor = fragment.getActivity().getContentResolver().query(phonesUri,
                projection, selection, selectionArgs, null);

        if (mCursor.moveToFirst()){
//            txtName.setText(mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME)));
        }

        ListAdapter adapter = new PhoneNumbersAdapter(fragment.getActivity(),
                R.layout.list_item_contact_numbers, mCursor,
                new String[] {ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER },
                new int[] { R.id.txtTitle, R.id.txtSubTitle});

    }

    public void listItemClicked2(TokenRouter router, int position) {

    }
}
