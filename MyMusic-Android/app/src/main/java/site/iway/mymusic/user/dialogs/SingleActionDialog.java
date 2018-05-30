package site.iway.mymusic.user.dialogs;

import android.content.Context;

public class SingleActionDialog extends ActionDialog {

    public SingleActionDialog(Context context) {
        super(context);
        setType(TYPE_SINGLE);
    }

}
