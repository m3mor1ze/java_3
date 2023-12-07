import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.*;

public class MainFrame extends JFrame {
    // Константы с исходным размером окна приложения
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private Double[] coefficients;// Массив коэффициентов многочлена
    private JFileChooser fileChooser = null;// Объект диалогового окна для выбора файлов. Компонент не создаѐтся изначально, т.к. может и не понадобиться пользователю если тот не собирается сохранять данные в файл

    private JMenuItem saveToTextMenuItem;// Элементы меню вынесены в поля данных класса, так как ими необходимо манипулировать из разных мест
    private JMenuItem aboutMenuItem;
    private JMenuItem saveToCSV;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;

    private JTextField textFieldFrom;// Поля ввода для считывания значений переменных
    private JTextField textFieldTo;
    private JTextField textFieldStep;

    private Box hBoxResult;
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();// Визуализатор ячеек таблицы
    private GornerTableModel data;// Модель данных с результатами вычислений

    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");// Обязательный вызов конструктора предка
        this.coefficients = coefficients;// Запомнить во внутреннем поле переданные коэффициенты

        setSize(WIDTH, HEIGHT);// Установить размеры окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2,(kit.getScreenSize().height - HEIGHT) / 2);// Отцентрировать окно приложения на экране

                                    //создание меню
        ////----------------------------------------------------------------------------------
        /**/    JMenuBar menuBar = new JMenuBar();// Создать меню
        /**/    setJMenuBar(menuBar);// Установить меню в качестве главного меню приложения
        /**/    JMenu fileMenu = new JMenu("Файл");// Добавить в меню пункт меню "Файл"
        /**/    menuBar.add(fileMenu);// Добавить его в главное меню
        /**/    JMenu tableMenu = new JMenu("Таблица");// Создать пункт меню "Таблица"
        /**/    menuBar.add(tableMenu);// Добавить его в главное меню
        /**/
        /**/    JMenu Info = new JMenu("Справка");// Добавить в меню пункт меню "Справка"
        /**/    menuBar.add(Info);// Добавить его в главное меню
        ////----------------------------------------------------------------------------------

                            //сохранение в текстовый файл
        ////----------------------------------------------------------------------------------
        /**/    Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();// Если экземпляр диалогового окна "Открыть файл" ещѐ не создан, то создать его
                    fileChooser.setCurrentDirectory(new File("."));// и инициализировать текущей директорией
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)// Показать диалоговое окно
                    saveToTextFile(fileChooser.getSelectedFile());// Если результат его показа успешный, сохранить данные в текстовый файл
            }
        };
        /**/    saveToTextMenuItem = fileMenu.add(saveToTextAction);// Добавить соответствующий пункт подменю в меню "Файл"
        /**/    saveToTextMenuItem.setEnabled(false);// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        ////----------------------------------------------------------------------------------

                                    //сохранение в CSV
        ////----------------------------------------------------------------------------------
        /**/    Action saveTOCSV = new AbstractAction("Сохранить в CSV") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser(); // Если экземпляр диалогового окна "Открыть файл" ещѐ не создан, то создать его
                    fileChooser.setCurrentDirectory(new File("."));// и инициализировать текущей директорией
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)// Показать диалоговое окно
                    saveToCSV(fileChooser.getSelectedFile());// Если результат его показа успешный, сохранить данные в текстовый файл
            }
        };
        /**/    saveToCSV = fileMenu.add(saveTOCSV);// Добавить соответствующий пункт подменю в меню "Файл"
        /**/    saveTOCSV.setEnabled(false);// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        ////----------------------------------------------------------------------------------

                                        //о программе
        ////----------------------------------------------------------------------------------
        /**/    Action about = new AbstractAction("О программе") {
            public void actionPerformed(ActionEvent event) {
                ImageIcon icon = new ImageIcon("res/logo.png");
                JOptionPane.showMessageDialog(MainFrame.this, "Danilin, 5 group", "О программе", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        };
        /**/    aboutMenuItem = Info.add(about);
        ////----------------------------------------------------------------------------------

                            //Сохранить данные для построения графика
        ////----------------------------------------------------------------------------------
        /**/    Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();// Если экземпляр диалогового окна "Открыть файл" ещѐ не создан, то создать его
                    fileChooser.setCurrentDirectory(new File("."));// и инициализировать текущей директорией
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)// Показать диалоговое окно
                    saveToGraphicsFile(fileChooser.getSelectedFile());// Если результат его показа успешный, сохранить данные в двоичный файл
            }
        };
        /**/    saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);// Добавить соответствующий пункт подменю в меню "Файл"
        /**/    saveToGraphicsMenuItem.setEnabled(false);// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        ////----------------------------------------------------------------------------------

                                    //Найти значение многочлена
        ////----------------------------------------------------------------------------------
        /**/    Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
            // Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска","Поиск значения", JOptionPane.QUESTION_MESSAGE);

                renderer.setNeedle(value);// Установить введенное значение в качестве иголки
                getContentPane().repaint();// Обновить таблицу
            }
        };
        /**/    searchValueMenuItem = tableMenu.add(searchValueAction);// Добавить действие в меню "Таблица"
        /**/    searchValueMenuItem.setEnabled(false);// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        ////----------------------------------------------------------------------------------

                            //Создание и настройка текстовых полей
        ////----------------------------------------------------------------------------------------------------------------
        /**/    JLabel labelForFrom = new JLabel("X изменяется на интервале от:");// Создать область с полями ввода для границ отрезка и шага создать подпись для ввода левой границы отрезка
        /**/    textFieldFrom = new JTextField("0.0", 10);// Создать текстовое поле для ввода значения длиной в 10 символов, установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        /**/    textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        /**/    JLabel labelForTo = new JLabel("до:");// Создать подпись для ввода левой границы отрезка
        /**/    textFieldTo = new JTextField("1.0", 10);// Создать текстовое поле для ввода значения длиной в 10 символов со значением по умолчанию 1.0
        /**/    textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());// Установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        /**/    JLabel labelForStep = new JLabel("с шагом:");// Создать подпись для ввода шага табулирования
        /**/    textFieldStep = new JTextField("0.1", 10);// Создать текстовое поле для ввода значения длиной в 10 символов со значением по умолчанию 1.0
        /**/    textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());// Установить максимальный размер равный предпочтительному, чтобы предотвратить увеличение размера поля ввода
        ////----------------------------------------------------------------------------------------------------------------

                                //Сборка контейнера
        ////----------------------------------------------------------------------------------------------------------------
        /**/    Box hboxRange = Box.createHorizontalBox();// Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        /**/    hboxRange.setBorder(BorderFactory.createBevelBorder(1));// Задать для контейнера тип рамки "объѐмная"
        /**/    hboxRange.add(Box.createHorizontalGlue());// Добавить "клей" C1-H1
        /**/    hboxRange.add(labelForFrom);// Добавить подпись "От"
        /**/    hboxRange.add(Box.createHorizontalStrut(10));// Добавить "распорку" C1-H2
        /**/    hboxRange.add(textFieldFrom);// Добавить поле ввода "От"
        /**/    hboxRange.add(Box.createHorizontalStrut(20));// Добавить "распорку" C1-H3
        /**/    hboxRange.add(labelForTo);// Добавить подпись "До"
        /**/    hboxRange.add(Box.createHorizontalStrut(10));// Добавить "распорку" C1-H4
        /**/    hboxRange.add(textFieldTo);// Добавить поле ввода "До"
        /**/    hboxRange.add(Box.createHorizontalStrut(20));// Добавить "распорку" C1-H5
        /**/    hboxRange.add(labelForStep);// Добавить подпись "с шагом"
        /**/    hboxRange.add(Box.createHorizontalStrut(10));// Добавить "распорку" C1-H6
        /**/    hboxRange.add(textFieldStep);// Добавить поле для ввода шага табулирования
        /**/    hboxRange.add(Box.createHorizontalGlue());// Добавить "клей" C1-H7
        ////----------------------------------------------------------------------------------------------------------------

        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при компоновке область совсем не сдавили
        hboxRange.setPreferredSize(new Dimension(new Double(hboxRange.getMaximumSize().getWidth()).intValue(),new Double(hboxRange.getMinimumSize().getHeight()).intValue() * 2));

        getContentPane().add(hboxRange, BorderLayout.NORTH);// Установить область в верхнюю (северную) часть компоновки

                        //Кнопки "Вычислить", "Очистить поля", "Выделить числа, близкие к простым"
        ////----------------------------------------------------------------------------------------------------------------
        /**/    JButton buttonCalc = new JButton("Вычислить");// Создать кнопку "Вычислить"
        /**/    // Задать действие на нажатие "Вычислить" и привязать к кнопке
        /**/    buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
            // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());

                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);// На основе считанных данных создать новый экземпляр модели таблицы
                    JTable table = new JTable(data);// Создать новый экземпляр таблицы
                    table.setDefaultRenderer(Double.class, renderer);// Установить в качестве визуализатора ячеек для класса Double разработанный визуализатор
                    table.setRowHeight(30);// Установить размер строки таблицы в 30 пикселов
                    hBoxResult.removeAll();// Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.add(new JScrollPane(table));// Добавить в hBoxResult таблицу, "обѐрнутую" в панель с полосами прокрутки
                    getContentPane().validate();// Обновить область содержания главного окна
                    saveToTextMenuItem.setEnabled(true);// Пометить ряд элементов меню как доступных
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                    saveTOCSV.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this,"Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        /**/    JButton buttonReset = new JButton("Очистить поля");// Создать кнопку "Очистить поля"
        /**/    // Задать действие на нажатие "Очистить поля" и привязать к кнопке
        /**/    buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
            // Удалить все вложенные элементы контейнера hBoxResult
                hBoxResult.removeAll();
            // Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
            // Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
                saveTOCSV.setEnabled(false);
                renderer.setChecker(false);
            // Обновить область содержания главного окна
                getContentPane().validate();
            }
        });
        /**/    JButton paintItCyan = new JButton("Выделить числа, близкие к простым");
        /**/    paintItCyan.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev) {
                renderer.setChecker(true);
                getContentPane().validate();
                getContentPane().validate();
                //renderer.primeNumberCheck();
                //renderer.getTableCellRendererComponent();

            }
        });
        /**/
        /**/    /// Поместить созданные кнопки в контейнер
        /**/    Box hboxButtons = Box.createHorizontalBox();
        /**/    hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        /**/    hboxButtons.add(Box.createHorizontalGlue());
        /**/    hboxButtons.add(buttonCalc);
        /**/    hboxButtons.add(Box.createHorizontalStrut(10));
        /**/    hboxButtons.add(buttonReset);
        /**/    hboxButtons.add(Box.createHorizontalStrut(10));
        /**/    hboxButtons.add(paintItCyan);
        /**/    hboxButtons.add(Box.createHorizontalGlue());
        ////----------------------------------------------------------------------------------------------------------------
        // Установить предпочтительный размер области равным удвоенному минимальному, чтобы при
        // компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(new Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new Double(hboxButtons.getMinimumSize().getHeight()).intValue() * 2));
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);// Разместить контейнер с кнопками в нижней (южной) области граничной компоновки
        hBoxResult = Box.createHorizontalBox();// Область для вывода результата пока что пустая
        hBoxResult.add(new JPanel());
        getContentPane().add(hBoxResult, BorderLayout.CENTER);// Установить контейнер hBoxResult в главной (центральной) области граничной компоновки
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
            // Создать новый байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            // Записать в поток вывода попарно значение X в точке, значение многочлена в точке
            for (int i = 0; i < data.getRowCount(); i++) {
                out.writeDouble((Double) data.getValueAt(i, 0));
                out.writeDouble((Double) data.getValueAt(i, 1));
            }
            out.close();// Закрыть поток вывода
        } catch (Exception e) {
            // Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
            // так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            PrintStream out = new PrintStream(selectedFile);// Создать новый символьный поток вывода, направленный в указанный файл
            // Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i = 0; i < coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" +
                        (coefficients.length - i - 1));
                if (i != coefficients.length - 1)
                    out.print(" + ");
            }
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " +
                    data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");
            // Записать в поток вывода значения в точках
            for (int i = 0; i < data.getRowCount(); i++) {
                out.println("Значение в точке " + data.getValueAt(i, 0)
                        + " равно " + data.getValueAt(i, 1));
            }

            out.close();// Закрыть поток
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file");
        }
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаѐм, а не открываем
    }

    protected void saveToCSV(File selectedFile) {
        try {
            // Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);

            for (int i = 0; i < data.getRowCount(); i++) {
                out.println(data.getValueAt(i, 0)
                        + "," + data.getValueAt(i, 1));
            }
            out.close();// Закрыть поток
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file");
        }
    }
}