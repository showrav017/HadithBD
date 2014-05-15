package com.ehsan.hadithbd;

/**
 * Created by Muhammad Ehsanul Hoq on 4/3/14.
 */
import java.util.EventListener;

public interface Layout_Listener extends EventListener {
    void onFired(String item);
}