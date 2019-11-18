package com.lixn.lambda.swing;

import javax.swing.*;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/11/10
 * @描述 Lambda表达式实践
 */
public class SwingTest {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("lixn JFrame");
        JButton jButton = new JButton("lixn JButton");
        //    jButton.addActionListener(
        //        new ActionListener() {
        //          @Override
        //          public void actionPerformed(ActionEvent e) {
        //            System.out.println("Button pressed!");
        //          }
        //        });
        jButton.addActionListener(
                event -> {
                    System.out.println("Button pressed!");
                });
        jFrame.add(jButton);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
