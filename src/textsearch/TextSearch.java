package textsearch;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSearch extends JFrame {

    final JTextArea textArea = new JTextArea();
    public JCheckBox checkBox = new JCheckBox("Use regex");
    public JTextField searchField = new JTextField();
    public static ArrayList<String> results = new ArrayList<>();
    public static ArrayList<Integer> startIndexes = new ArrayList<>();
    public static int signedResult = 0;

    public TextSearch() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 550);
        setTitle("Text Editor");
        Dimension dFrame = new Dimension(550, 550);
        setMinimumSize(dFrame);
        setLocationRelativeTo(null);

        JPanel jpanel = new JPanel();
        jpanel.setBounds(0, 0, 350, 100);
        add(jpanel, BorderLayout.NORTH);

        //file chooser
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        this.add(jfc);

        //open button
        ImageIcon openIcon = new ImageIcon(new ImageIcon("src/open2.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        JButton openButton = new JButton(openIcon);
        openButton.setName("OpenButton");
        Dimension dl = new Dimension(30, 30);
        openButton.setPreferredSize(dl);
        openButton.addActionListener(e -> {
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = jfc.getSelectedFile();
                    textArea.setText(new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()))));
                } catch (IOException fileNotFoundException) {
                    textArea.setText("");
                    fileNotFoundException.printStackTrace();
                }
            }
        });
        jpanel.add(openButton);

        //save button
        ImageIcon saveIcon = new ImageIcon(new ImageIcon("src/save2.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        JButton saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(dl);
        saveButton.addActionListener(e -> {
            int returnValue = jfc.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String filename = jfc.getSelectedFile().getName();
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write(textArea.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        jpanel.add(saveButton);

        //search field
        searchField.setBounds(5, 5, 100, 90);
        searchField.setName("SearchField");
        Dimension d = new Dimension(120, 28);
        searchField.setPreferredSize(d);
        jpanel.add(searchField);

        //search button
        ImageIcon searchIcon = new ImageIcon(new ImageIcon("src/search.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        JButton startSearchButton = new JButton(searchIcon);
        saveButton.setName("StartSearchButton");
        saveButton.setPreferredSize(dl);
        startSearchButton.addActionListener(e -> {
            if (searchField.getText() != null) {
                String text = searchField.getText();
                results.clear();
                startIndexes.clear();
                signedResult = 0;
                (new SearchText(text, textArea)).execute();
            }
        });
        jpanel.add(startSearchButton);

        //previous button
        ImageIcon prevIcon = new ImageIcon(new ImageIcon("src/previous.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        JButton prevButton = new JButton(prevIcon);
        saveButton.setName("PreviousMatchButton");
        saveButton.setPreferredSize(dl);
        prevButton.addActionListener(e -> {
            if (!results.isEmpty()) {
                if (signedResult > 0) {
                    signedResult--;
                } else if (signedResult == 0) {
                    signedResult = results.size() - 1;
                }
                int from = startIndexes.get(signedResult);
                int to = startIndexes.get(signedResult) + results.get(signedResult).length();
                textArea.setCaretPosition(to);
                textArea.select(from, to);
                textArea.grabFocus();
            }
        });
        jpanel.add(prevButton);

        //next button
        ImageIcon nextIcon = new ImageIcon(new ImageIcon("src/next2.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        JButton nextButton = new JButton(nextIcon);
        saveButton.setName("NextMatchButton");
        saveButton.setPreferredSize(dl);
        nextButton.addActionListener(e -> {
            if (!results.isEmpty()) {
                if (signedResult < results.size() - 1) {
                    signedResult++;
                } else if (signedResult == results.size() - 1) {
                    signedResult = 0;
                }
                int from = startIndexes.get(signedResult);
                int to = startIndexes.get(signedResult) + results.get(signedResult).length();
                textArea.setCaretPosition(to);
                textArea.select(from, to);
                textArea.grabFocus();
            }
        });
        jpanel.add(nextButton);

        //checkbox for regular expression
        jpanel.add(checkBox);

        //menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        //menu, file menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        menuBar.add(fileMenu);

        //open menu item
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");

        openMenuItem.addActionListener(e -> {
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = jfc.getSelectedFile();
                    textArea.setText(new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()))));
                } catch (IOException fileNotFoundException) {
                    textArea.setText("");
                    fileNotFoundException.printStackTrace();
                }
            }
        });

        //save menu item
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(e -> {
            int returnValue = jfc.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String filename = jfc.getSelectedFile().getName();
                try (FileWriter fw = new FileWriter(filename, false)) {
                    fw.write(textArea.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        //exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(event -> {
            dispose();
            System.exit(0);
        });

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        //search menu
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        menuBar.add(searchMenu);

        //start search menu item
        JMenuItem startSearch = new JMenuItem("Start search");
        openMenuItem.setName("MenuStartSearch");
        startSearch.addActionListener(e -> {
            if (searchField.getText() != null) {
                String text = searchField.getText();
                results.clear();
                startIndexes.clear();
                signedResult = 0;
                (new SearchText(text, textArea)).execute();
            }
        });

        //prev menu item
        JMenuItem previousMatch = new JMenuItem("Previous match");
        openMenuItem.setName("MenuPreviousMatch");
        previousMatch.addActionListener(e -> {
            if (!results.isEmpty()) {
                if (signedResult > 0) {
                    signedResult--;
                } else if (signedResult == 0) {
                    signedResult = results.size() - 1;
                }
                int from = startIndexes.get(signedResult);
                int to = startIndexes.get(signedResult) + results.get(signedResult).length();
                textArea.setCaretPosition(to);
                textArea.select(from, to);
                textArea.grabFocus();
            }
        });

        //next menu item
        JMenuItem nextMatch = new JMenuItem("Next match");
        openMenuItem.setName("MenuNextMatch");
        nextMatch.addActionListener(e -> {
            if (!results.isEmpty()) {
                if (signedResult < results.size() - 1) {
                    signedResult++;
                } else if (signedResult == results.size() - 1) {
                    signedResult = 0;
                }
                int from = startIndexes.get(signedResult);
                int to = startIndexes.get(signedResult) + results.get(signedResult).length();
                textArea.setCaretPosition(to);
                textArea.select(from, to);
                textArea.grabFocus();
            }
        });

        //regexp menu item
        JMenuItem useRegExp = new JMenuItem("Use regular expression");
        openMenuItem.setName("MenuUseRegExp");
        useRegExp.addActionListener(e ->
                checkBox.setSelected(!checkBox.isSelected())
        );

        searchMenu.add(startSearch);
        searchMenu.add(previousMatch);
        searchMenu.add(nextMatch);
        searchMenu.add(useRegExp);

        //text area
        textArea.setName("TextArea");
        add(textArea, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Border border = scrollPane.getBorder();
        Border marginBorder = new EmptyBorder(new Insets(5, 15, 15, 15));
        scrollPane.setBorder(border == null ? marginBorder : new CompoundBorder(marginBorder, border));
        add(scrollPane);

        setVisible(true);
    }

    //search thread
    static class SearchText extends SwingWorker<Integer, Object> {
        String text;
        JTextArea textArea;
        Integer index = -2;
        MatchResult result;

        public SearchText(String text, JTextArea textArea) {
            this.text = text;
            this.textArea = textArea;
        }

        @Override
        public Integer doInBackground() {
            Pattern pattern = Pattern.compile(text);
            Matcher matcher = pattern.matcher(textArea.getText());
            while (matcher.find()) {
                result = matcher.toMatchResult();
                results.add(matcher.group());
                startIndexes.add(matcher.start());
            }
            if (!startIndexes.isEmpty()) {
                index = startIndexes.get(0);
            }
            return index;
        }

        @Override
        protected void done() {
            if (result != null) {
                textArea.setCaretPosition(index + result.group(0).length());
                textArea.select(index, index + result.group(0).length());
                textArea.grabFocus();
            }
        }
    }
}
