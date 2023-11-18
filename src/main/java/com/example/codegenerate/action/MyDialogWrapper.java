package com.example.codegenerate.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyDialogWrapper extends DialogWrapper {
    private final ArrayList<JCheckBox> checkBoxPanels = new ArrayList<>();

    private final JTextArea textArea = new JTextArea(5, 30);

    private Editor editor;

    public MyDialogWrapper(Editor editor) {
        super(true);
        this.editor = editor;
        setTitle("FieldTranslator");
        init();
    }

    public ArrayList<JCheckBox> getCheckBoxPanels() {
        return checkBoxPanels;
    }

    public String getText() {
        return textArea.getText();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JCheckBox checkBox1 = new JCheckBox("field");
        checkBox1.setSelected(Boolean.TRUE);
        checkBox1.setEnabled(false);
        JCheckBox checkBox2 = new JCheckBox("type");
        checkBox2.setSelected(Boolean.TRUE);
        JCheckBox checkBox3 = new JCheckBox("comment");
        checkBox3.setSelected(Boolean.TRUE);

        checkBoxPanels.add(checkBox1);
        checkBoxPanels.add(checkBox2);
        checkBoxPanels.add(checkBox3);

        JPanel checkPanel = new JPanel();
        JButton reverse = new JButton("reverse");
        JButton button = new JButton("rotate");
        checkPanel.setLayout(new FlowLayout());


        reverse.addActionListener(e -> {
            reverseCheckPanel(checkPanel, button,reverse);
        });
        button.addActionListener(e -> {
            rotateCheckPanel(checkPanel, button,reverse);
        });

        rotateCheckPanel(checkPanel, button,reverse);
        panel.add(checkPanel);
        panel.add(new JScrollPane(textArea));
        return panel;
    }

    private void reverseCheckPanel(JPanel panel, JButton button, JButton reverse) {
        panel.removeAll();
        Collections.reverse(checkBoxPanels);
        checkBoxPanels.forEach(panel::add);
        panel.add(button);
        panel.add(reverse);
        panel.revalidate();
        panel.repaint();
    }


    private void rotateCheckPanel(JPanel panel, JButton button, JButton reverse) {
        panel.removeAll();
        Collections.rotate(checkBoxPanels, 1);
        checkBoxPanels.forEach(panel::add);
        panel.add(button);
        panel.add(reverse);
        panel.revalidate();
        panel.repaint();
    }

    @Override
    protected void doOKAction(){
        if (textArea.getText() != null && !textArea.getText().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            try {
                String[] lines = textArea.getText().split("\n");
                java.util.List<Map<String, String>> contentMap = new ArrayList<>();
                for (String line : lines) {
                    String[] parts = line.trim().split("\\s+");
                    int partsIndex = 0;
                    Map<String, String> map = new HashMap<>();
                    for (JCheckBox checkBox : checkBoxPanels) {
                        if (checkBox.isSelected()) {
                            map.put(checkBox.getText(), parts[partsIndex]);
                            partsIndex++;
                        } else if (checkBox.getText() == "type") {
                            map.put(checkBox.getText(), "String");
                        }
                    }
                    contentMap.add(map);
                }

                for (Map<String, String> map : contentMap) {
                    if (map.containsKey("comment")) {
                        sb.append("\n\t/** \n\t *").append(map.get("comment")).append("\n\t */");
                    }
                    sb.append("\n\tprivate ").append(map.get("type")).append(" ").append(map.get("field")).append(";\n");
                }
            } catch (Exception e1) {
                hintError();
                return;
            }
            if (editor != null) {
                this.dispose();
                final Document document = editor.getDocument();
                final CaretModel caretModel = editor.getCaretModel();
                final int offset = caretModel.getOffset();
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () ->
                        document.insertString(offset, sb.toString())
                );
            }
        }

    }

    public void hintError() {
        textArea.setText("ops!!!, try again or please text author for debug~~~~");
    }
}