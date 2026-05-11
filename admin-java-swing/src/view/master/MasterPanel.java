package view.master;

import javax.swing.*;

public interface MasterPanel {
    String getPanelName();
    void refreshData();
    default void onPanelShown() {}
    default void onPanelHidden() {}
    JPanel getContentPanel();
}