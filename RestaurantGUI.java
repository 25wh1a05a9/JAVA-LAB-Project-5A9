import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class RestaurantGUI extends JFrame {
    private Restaurant rest = new KFC();
    private final Map<Integer, Integer> cart = new LinkedHashMap<>();
    private int lastOrderId = -1;

    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Type", "Price"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DefaultListModel<String> cartModel = new DefaultListModel<>();
    private final JTextField qty = new JTextField("0"), people = new JTextField("1", 3);
    private final JComboBox<String> restBox = new JComboBox<>(new String[]{"KFC", "Dominos"});
    private final JComboBox<String> discBox = new JComboBox<>(new String[]{"No discount", "Flat Rs.50 off", "10% off"});
    private final JTextArea out = new JTextArea(12, 44);

    public RestaurantGUI() {
        super("Restaurant Ordering & Billing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // top bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemBtn = new JButton("Add Menu Item");
        top.add(new JLabel("Restaurant:")); top.add(restBox); top.add(addItemBtn);
        add(top, BorderLayout.NORTH);

        // menu table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // quantity keypad (GridLayout) + cart
        JPanel right = new JPanel(new BorderLayout(6, 6));
        qty.setEditable(false);
        qty.setHorizontalAlignment(JTextField.RIGHT);
        qty.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        JPanel keys = new JPanel(new GridLayout(4, 3, 4, 4));
        for (String k : new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "Add"}) {
            JButton b = new JButton(k);
            b.addActionListener(e -> key(k));
            keys.add(b);
        }
        JScrollPane cartPane = new JScrollPane(new JList<>(cartModel));
        cartPane.setPreferredSize(new Dimension(200, 120));
        cartPane.setBorder(BorderFactory.createTitledBorder("Cart"));
        right.add(qty, BorderLayout.NORTH);
        right.add(keys, BorderLayout.CENTER);
        right.add(cartPane, BorderLayout.SOUTH);
        add(right, BorderLayout.EAST);

        // bottom: actions + output
        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton placeBtn = new JButton("Place Order"), clearBtn = new JButton("Clear Cart");
        JButton billBtn = new JButton("Generate Bill"), splitBtn = new JButton("Split");
        actions.add(discBox); actions.add(placeBtn); actions.add(clearBtn); actions.add(billBtn);
        actions.add(new JLabel("Split among:")); actions.add(people); actions.add(splitBtn);
        out.setEditable(false);
        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(new JScrollPane(out), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // listeners
        restBox.addActionListener(e -> {
            rest = restBox.getSelectedIndex() == 0 ? new KFC() : new Dominos();
            cart.clear(); refreshCart(); lastOrderId = -1; out.setText(""); loadMenu();
        });
        addItemBtn.addActionListener(e -> addMenuItem());
        placeBtn.addActionListener(e -> placeOrder());
        clearBtn.addActionListener(e -> { cart.clear(); refreshCart(); });
        billBtn.addActionListener(e -> showBill());
        splitBtn.addActionListener(e -> split());

        loadMenu();
        pack();
        setLocationRelativeTo(null);
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void key(String k) {
        if (k.equals("C")) qty.setText("0");
        else if (k.equals("Add")) addToCart();
        else if (qty.getText().length() < 3) qty.setText(qty.getText().equals("0") ? k : qty.getText() + k);
    }

    private void loadMenu() {
        model.setRowCount(0);
        for (Item i : rest.getMenu())
            model.addRow(new Object[]{i.getId(), i.getName(), i.getCategory(), i.isVeg() ? "Veg" : "Non-veg", String.format("%.2f", i.getPrice())});
    }

    private void refreshCart() {
        cartModel.clear();
        for (Map.Entry<Integer, Integer> e : cart.entrySet())
            for (Item i : rest.getMenu())
                if (i.getId() == e.getKey()) cartModel.addElement(e.getValue() + " x " + i.getName());
    }

    private void addToCart() {
        int row = table.getSelectedRow();
        if (row < 0) { err("Select a menu item first."); return; }
        int q = Integer.parseInt(qty.getText());
        if (q <= 0) { err("Enter a quantity using the keypad."); return; }
        cart.merge((Integer) model.getValueAt(row, 0), q, Integer::sum);
        qty.setText("0");
        refreshCart();
    }

    private void addMenuItem() {
        String s = JOptionPane.showInputDialog(this, "Enter: name,price,category,veg(y/n)");
        if (s == null) return;
        try {
            String[] p = s.split(",");
            rest.addItem(new Item(Reports.nextId(rest.getMenu()), p[0].trim(), Double.parseDouble(p[1].trim()), p[2].trim(), p[3].trim().equalsIgnoreCase("y")));
            loadMenu();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) { err("Use format: name,price,category,y/n");
        } catch (InvalidOrderException ex) { err(ex.getMessage()); }
    }

    private void placeOrder() {
        int d = discBox.getSelectedIndex();
        Discount disc = (d == 1) ? new FlatDiscount(50) : (d == 2) ? new PercentDiscount(10) : new NoDiscount();
        try {
            lastOrderId = rest.placeOrder(new LinkedHashMap<>(cart), disc).getOrderId();
            out.setText("Order placed. Order id = " + lastOrderId + "\nClick 'Generate Bill' to view the bill.");
            cart.clear(); refreshCart();
        } catch (InvalidOrderException ex) { err(ex.getMessage()); }
    }

    private void showBill() {
        if (lastOrderId < 0) { err("Place an order first."); return; }
        try { out.setText(rest.billText(lastOrderId)); } catch (InvalidOrderException ex) { err(ex.getMessage()); }
    }

    private void split() {
        if (lastOrderId < 0) { err("Place an order first."); return; }
        try {
            int n = Integer.parseInt(people.getText().trim());
            if (n == 0) throw new ArithmeticException("Cannot split a bill among 0 people.");
            if (n < 0) throw new ArithmeticException("Number of people must be positive.");
            out.append(String.format(Locale.US, "%nSplit among %d: Rs.%.2f each%n", n, rest.generateBill(lastOrderId) / n));
        } catch (NumberFormatException ex) { err("Enter a whole number.");
        } catch (ArithmeticException | InvalidOrderException ex) { err(ex.getMessage()); }
    }
}
