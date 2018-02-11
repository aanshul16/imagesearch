package imageserach.fieldwire.amko0l.com.imagesearch.utils;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mmt5544 on 11/2/18.
 */

public class FragmentManagerUtil {
    public static void createFragment(int id, Fragment pFragment, String tagName, AppCompatActivity context, boolean backStateMaintained) {
        if (isActivityActive(context)) {
            FragmentManager fm = context.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (backStateMaintained) {
                ft.addToBackStack(tagName);
            }
            ft.replace(id, pFragment, tagName);
            ft.commitAllowingStateLoss();
        }
    }

    public static boolean isActivityActive(Activity activity) {
        return !(activity == null || activity.isFinishing()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                activity.isDestroyed());
    }
}
