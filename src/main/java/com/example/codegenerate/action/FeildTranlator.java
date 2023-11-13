package com.example.codegenerate.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;

public class FeildTranlator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MyDialogWrapper dialog = new MyDialogWrapper(e.getData(PlatformDataKeys.EDITOR));
        dialog.show();
    }


}
