package bsu.rfe.java.group8.lab45.Maziar.varB6;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    // Начальные размеры окна приложения
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    // Объект диалогового окна для выбора файлов
    private JFileChooser fileChooser = null;
    // Пункты меню
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showIntGraphicsMenuItem;
    // Компонент-отображатель графика

    // Флаг, указывающий на загруженность данных графика
    private boolean fileLoaded = false;

    public MainFrame() {
        // Вызов конструктора предка Frame
        super("Построение графиков функций на основе заранее подготовленных файлов");
        // Установка размеров окна
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH) / 2,
                (kit.getScreenSize().height - HEIGHT) / 2);
        // Развѐртывание окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);
        // Создать и установить полосу меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // Добавить пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // Создать действие по открытию файла
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        };
        // Добавить соответствующий элемент меню
        fileMenu.add(openGraphicsAction);
        // Создать пункт меню "График"
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        // Создать действие для реакции на активацию элемента "Показывать оси координат"
        Action showAxisAction = new AbstractAction("Показывать оси координат") {
            public void actionPerformed(ActionEvent event) {
                // свойство showAxis класса GraphicsDisplay истина, если элемент меню
                // showAxisMenuItem отмечен флажком, и ложь - в противном случае

                //COMMING SOON
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        // Добавить соответствующий элемент в меню
        graphicsMenu.add(showAxisMenuItem);
        // Элемент по умолчанию включен (отмечен флажком)
        showAxisMenuItem.setSelected(true);
        // Повторить действия для элемента "Показывать маркеры точек"
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                // по аналогии с showAxisMenuItem

                //COMMING SOON
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        // Элемент по умолчанию включен (отмечен флажком)
        showMarkersMenuItem.setSelected(true);
        // Создать действие для элемента "Отобразить функцию «Целая часть f»"
        Action showIntGraphicsAction = new AbstractAction("Отобразить функцию «Целая часть f»") {
            public void actionPerformed(ActionEvent e) {

                //COMMING SOON
            }
        };
        showIntGraphicsMenuItem = new JCheckBoxMenuItem(showIntGraphicsAction);
        graphicsMenu.add(showIntGraphicsMenuItem);
        showIntGraphicsMenuItem.setSelected(false);
        // Зарегистрировать обработчик событий, связанных с меню "График"

        //COMMING SOON
    }


    // Считывание данных графика из существующего файла
    protected void openGraphics(File selectedFile) {
        try {

            // Шаг 1 - Открыть поток чтения данных, связанный с входным файловым потоком
            DataInputStream in = new DataInputStream(new
                    FileInputStream(selectedFile));
            /* Шаг 2 - Зная объѐм данных в потоке ввода можно вычислить,
             * сколько памяти нужно зарезервировать в массиве:
             * Всего байт в потоке - in.available() байт;
             * Размер одного числа Double - Double.SIZE бит, или Double.SIZE/8 байт;
             * Так как числа записываются парами, то число пар меньше в 2 раза
             */
            Double[][] graphicsData = new
                    Double[in.available() / (Double.SIZE / 8) / 2][];
            // Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            int i = 0;
            while (in.available() > 0) {
                // Первой из потока читается координата точки X
                Double x = in.readDouble();
                // Затем - значение графика Y в точке X
                Double y = in.readDouble();
                System.out.println(x + " " + y);
                // Прочитанная пара координат добавляется в массив
                graphicsData[i++] = new Double[]{x, y};
            }
            // Шаг 4 - Проверка, имеется ли в списке в результате чтения хотя бы одна пара координат
            if (graphicsData != null && graphicsData.length > 0) {
                // Да - установить флаг загруженности данных
                fileLoaded = true;
                // Вызывать метод отображения графика


                //COMMING SOON
            }
            // Шаг 5 - Закрыть входной поток
            in.close();
        } catch (FileNotFoundException ex) {
            // В случае исключительной ситуации типа "Файл не найден" показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный " +
                    "файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
            // В случае ошибки ввода из файлового потока показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения " +
                            "координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    }
    // Класс-слушатель событий, связанных с отображением меню
    private class GraphicsMenuListener implements MenuListener {
        // Обработчик, вызываемый перед показом меню
        public void menuSelected(MenuEvent e) {
            // Доступность или недоступность элементов меню "График" определяется загруженностью данных
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
            showIntGraphicsMenuItem.setEnabled(fileLoaded);
        }
        public void menuDeselected(MenuEvent e)
        {
        }
        public void menuCanceled(MenuEvent e)
        {
        }
    }


}




