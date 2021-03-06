
package com.ksondzyk.gui_swing.frames;


import com.ksondzyk.entities.Message;
import com.ksondzyk.entities.Packet;
import com.ksondzyk.gui_swing.Storage;
import com.ksondzyk.network.TCP.TCPClientThread;
import com.ksondzyk.storage.Product;
import com.ksondzyk.utilities.CipherMy;
import org.json.JSONObject;

import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class importProductFrame extends JFrame {

    /**
     * Creates new form importProductFrame
     */
    public importProductFrame() {
        super("Логістика");
        initComponents();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SpinnerNumberModel sm = new SpinnerNumberModel(0, 0, 100000, 1);
        numberOfProductsSpinner = new JSpinner(sm);
        productChooser = new JComboBox<>();
        jSeparator1 = new JSeparator();
        subtractProductsButton = new JButton();
        addProductsButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setType(Type.POPUP);
        String[] temp = new String[Storage.products.size()];
        for (int i = 0; i < temp.length; i++){
            temp[i] = Storage.products.get(i).getName();
        }
        productChooser.setModel(new DefaultComboBoxModel<>(temp));
        subtractProductsButton.setText("Зняти з обліку");
        subtractProductsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtractProductsButtonActionPerformed(evt);
            }
        });

        addProductsButton.setText("Додати до обліку");
        addProductsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductsButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(productChooser, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(numberOfProductsSpinner, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addProductsButton, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subtractProductsButton, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfProductsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(productChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(subtractProductsButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addComponent(addProductsButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    /**
     * proceeds to subtracting the product
     * @param evt
     */
    private void subtractProductsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtractProductsButtonActionPerformed
        // TODO add your handling code here:
        String item = productChooser.getSelectedItem().toString();
        int numb = (int) numberOfProductsSpinner.getValue();
        int num = 0;
        int id = 0;
        for (Product p : Storage.products) {
            if (p.getName().equals(item)){
                if(numb>(p.getAmount())) {
                    showMessageDialog(null, "The quantity can`t be negative!");
                    dispose();
                    return;
                }
                if (p.getAmount()>=numb) {
                    id = p.getId();
                    num = (p.getAmount() - numb);
                    p.setAmount(num);
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cType","3");
        jsonObject.put("type","good");
        jsonObject.put("id",id);
        jsonObject.put("quantity",num);


        Packet packet = new Packet((byte) 1,new Message(1,1,jsonObject.toString(),false));
        TCPClientThread tcpClientThread = new TCPClientThread(packet);
        Packet answer = tcpClientThread.send();
        String jsonString = CipherMy.decode(answer.getBMsq().getMessage());
        JSONObject responseMessage = new JSONObject(jsonString);

        StorageFrame.productsTable.revalidate();
        StorageFrame.productsTable.repaint();

        Storage.model.fireTableDataChanged();
        dispose();

    }

    /**
     * proceeds to adding the product
     * @param evt
     */
    private void addProductsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductsButtonActionPerformed
        // TODO add your handling code here:
        String item = productChooser.getSelectedItem().toString();
        int numb = (int) numberOfProductsSpinner.getValue();
        int num = 0;
        int id = 0;
        for (Product p : Storage.products) {
            if (p.getName().equals(item)){
                id = p.getId();
                num = (p.getAmount()+numb);
                p.setAmount(num);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cType","3");
        jsonObject.put("type","good");
        jsonObject.put("id",id);
        jsonObject.put("quantity",num);


        Packet packet = new Packet((byte) 1,new Message(1,1,jsonObject.toString(),false));
        TCPClientThread tcpClientThread = new TCPClientThread(packet);
        Packet answer = tcpClientThread.send();
        String jsonString = CipherMy.decode(answer.getBMsq().getMessage());
        JSONObject responseMessage = new JSONObject(jsonString);

        StorageFrame.productsTable.revalidate();
        StorageFrame.productsTable.repaint();


        Storage.model.fireTableDataChanged();
        dispose();
    }



    // Variables declaration - do not modify
    private JButton addProductsButton;
    private JSeparator jSeparator1;
    private JSpinner numberOfProductsSpinner;
    private JComboBox<String> productChooser;
    private JButton subtractProductsButton;
    // End of variables declaration
}
