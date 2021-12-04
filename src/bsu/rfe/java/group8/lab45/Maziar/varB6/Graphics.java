package bsu.rfe.java.group8.lab45.Maziar.varB6;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EmptyStackException;
import java.util.Stack;
import javax.swing.JPanel;

class Graphic extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showIntGraphics = false;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    private double scaleX ;
    private double scaleY ;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke selStroke;
    private BasicStroke graphicsIntStroke;
    private Font captionFont;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    // Различные шрифты отображения надписей
    private Font axisFont;
    class GraphPoint {
        double xd;
        double yd;
        int x;
        int y;
        int n;
    }

    class Zone {
        double MAXY;
        double MINY;
        double MAXX;
        double MINX;
        boolean use;
    }
    private Zone zone = new Zone();
    private boolean transform = false;
    private boolean zoom = false;
    private boolean selMode = false;
    private boolean dragMode = false;
    private int mausePX = 0;
    private int mausePY = 0;
    private GraphPoint SMP;
    private Stack<Zone> stack = new Stack<Zone>();
    private int[][] graphicsDataI;
    private Rectangle2D.Double rect;


    public Graphic() {
        // Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
        // Сконструировать необходимые объекты, используемые в рисовании
        // Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[] {2,1,1,1,1,1,1,1,2,1,1,1,2}, 0.0f);
        // Перо для рисования целой части графика
        graphicsIntStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        // Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
        captionFont = new Font("Serif", Font.BOLD, 14);

        //

        selStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[]{8, 8}, 0.0f);


        MouseMotionHandler mouseMotionHandler = new MouseMotionHandler();
        addMouseMotionListener(mouseMotionHandler);
        addMouseListener(mouseMotionHandler);
        rect = new Rectangle2D.Double();
        zone.use = false;
    }

    // Данный метод вызывается из обработчика элемента меню "Открыть файл с графиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
        graphicsDataI = new int[graphicsData.length][2];
        // Запросить перерисовку компонента, т.е. неявно вызвать paintComponent()
        repaint();
    }

    // Методы-модификаторы для изменения параметров отображения графика
    // Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }
    public void setTransform(boolean transform) {
        this.transform = transform;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void setShowIntGraphics(boolean showIntGraphics) {
        this.showIntGraphics = showIntGraphics;
        repaint();
    }

    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {
        /* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона
         * Эта функциональность - единственное, что осталось в наследство от
         * paintComponent класса JPanel
         */
        super.paintComponent(g);
        // Шаг 2 - Если данные графика не загружены (при показе компонента при запуске программы) - ничего не делать
        if (graphicsData == null || graphicsData.length == 0) return;
        // Шаг 3 - Определить минимальное и максимальное значения для координат X и Y
        // Это необходимо для определения области пространства, подлежащей отображению
        // Еѐ верхний левый угол это (minX, maxY) - правый нижний это (maxX, minY)
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        if (zone.use) {
            minX = zone.MINX;
        }
        if (zone.use) {
            maxX = zone.MAXX;
        }
        minY = graphicsData[0][1];
        maxY = minY;
        // Найти минимальное и максимальное значение функции
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        if (!transform)
            scaleX *= getSize().getWidth();
        else
            scaleX *= getSize().getHeight();
        if (!transform)
            scaleY *= getSize().getHeight();
        else
            scaleY *= getSize().getWidth();
        if (transform) {
            ((Graphics2D) g).rotate(-Math.PI / 2);
            ((Graphics2D) g).translate(-getHeight(), 0);
        }
        scale = Math.min(scaleX, scaleY);
        /* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X и Y - сколько пикселов
         * приходится на единицу длины по X и по Y
         */
        scaleX = getSize().getWidth() / (maxX - minX);
        scaleY = getSize().getHeight() / (maxY - minY);
        // Шаг 5 - Чтобы изображение было неискажѐнным - масштаб должен быть одинаков
        // Выбираем за основу минимальный
        scale = Math.min(scaleX, scaleY);
        // Шаг 6 - корректировка границ отображаемой области согласно выбранному масштабу
        if (scale == scaleX) {
            /* Если за основу был взят масштаб по оси X, значит по оси Y делений меньше,
             * т.е. подлежащий визуализации диапазон по Y будет меньше высоты окна.
             * Значит необходимо добавить делений, сделаем это так:
             * 1) Вычислим, сколько делений влезет по Y при выбранном масштабе - getSize().getHeight()/scale
             * 2) Вычтем из этого сколько делений требовалось изначально
             * 3) Набросим по половине недостающего расстояния на maxY и minY
             */
            double yIncrement = (getSize().getHeight() / scale - (maxY -
                    minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            // Если за основу был взят масштаб по оси Y, действовать по аналогии
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        if (!zoom) {
            if (scale == scaleX) {
                double yIncrement = 0;
                if (!transform)
                    yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
                else
                    yIncrement = (getSize().getWidth() / scale - (maxY - minY)) / 2;
                maxY += yIncrement;
                minY -= yIncrement;
            }
            if (scale == scaleY) {
                double xIncrement = 0;
                if (!transform) {
                    xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                } else {
                    xIncrement = (getSize().getHeight() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                }
            }
        }
        // Шаг 7 - Сохранить текущие настройки холста
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        // Шаг 8 - В нужном порядке вызвать методы отображения элементов графика
        // Порядок вызова методов имеет значение, т.к. предыдущий рисунок будет затираться последующим
        // Первыми (если нужно) отрисовываются оси координат.
        if (showAxis) paintAxis(canvas);
        // Затем отображается сам график
        paintGraphics(canvas);
        // А затем график "целая часть функции"
        if (showIntGraphics) paintIntUnitOfGraphics(canvas);
        // Затем (если нужно) отображаются маркеры точек, по которым строился график.
        if (showMarkers) paintMarkers(canvas);
        if (selMode) {
            canvas.setColor(Color.BLACK);
            canvas.setStroke(selStroke);
            canvas.draw(rect);
        }
        if (SMP != null)
            paintHint(canvas);
        // Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintHint(Graphics2D canvas) {
        Color oldColor = canvas.getColor();
        canvas.setColor(Color.MAGENTA);
        StringBuffer label = new StringBuffer();
        label.append("X=");
        label.append(formatter.format((SMP.xd)));
        label.append(", Y=");
        label.append(formatter.format((SMP.yd)));
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds = captionFont.getStringBounds(label.toString(), context);
        if (!transform) {
            int dy = -10;
            int dx = +7;
            if (SMP.y < bounds.getHeight())
                dy = +13;
            if (getWidth() < bounds.getWidth() + SMP.x + 20)
                dx = -(int) bounds.getWidth() - 15;
            canvas.drawString(label.toString(), SMP.x + dx, SMP.y + dy);
        } else {
            int dy = 10;
            int dx = -7;
            if (SMP.x < 10)
                dx = +13;
            if (SMP.y < bounds.getWidth() + 20)
                dy = -(int) bounds.getWidth() - 15;
            canvas.drawString(label.toString(), getHeight() - SMP.y + dy, SMP.x + dx);
        }
        canvas.setColor(oldColor);
    }

    // Отрисовка графика по прочитанным координатам
    protected void paintGraphics(Graphics2D canvas) {
        // Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
        // Выбрать цвет линии
        canvas.setColor(Color.RED);
        /* Будем рисовать линию графика как путь, состоящий из множества сегментов (GeneralPath)
         * Начало пути устанавливается в первую точку графика, после чего прямой соединяется со
         * следующими точками
         */
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            // Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            graphicsDataI[i][0] = (int) point.getX();
            graphicsDataI[i][1] = (int) point.getY();
            if (transform) {
                graphicsDataI[i][0] = (int) point.getY();
                graphicsDataI[i][1] = getHeight() - (int) point.getX();
            }
            if (i > 0) {
                // Не первая итерация цикла - вести линию в точку point
                graphics.lineTo(point.getX(), point.getY());
            } else {
                // Первая итерация цикла - установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        // Отобразить график
        canvas.draw(graphics);
    }
    protected void paintIntUnitOfGraphics(Graphics2D canvas) {
        // Выбрать линию для рисования графика
        canvas.setStroke(graphicsIntStroke);
        // Выбрать цвет линии
        canvas.setColor(Color.blue);
        GeneralPath intGraphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            // Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1].intValue());
            if (i > 0) {
                if (graphicsData[i][1].intValue() == graphicsData[i - 1][1].intValue())
                    intGraphics.lineTo(point.getX(), point.getY());
                else {
                    intGraphics.lineTo(point.getX(),
                            xyToPoint(graphicsData[i][0], graphicsData[i - 1][1].intValue()).getY());
                    intGraphics.moveTo(point.getX(), point.getY());
                }
            } else {
                // Первая итерация цикла - установить начало пути в точку point
                intGraphics.moveTo(point.getX(), point.getY());
            }
        }
        // Отобразить график
        canvas.draw(intGraphics);

    }

    protected boolean isSumLessThanTen(Double[] point) {
        int valueFuncInt = point[1].intValue();
        int sum = 0;
        while(valueFuncInt > 0) {
            sum += valueFuncInt % 10;
            valueFuncInt /= 10;
        }
        return sum < 10 ? true : false;
    }
    // Отображение маркеров точек, по которым рисовался график
    protected void paintMarkers(Graphics2D canvas) {
        // Шаг 1 - Установить специальное перо для черчения контуров маркеров
        canvas.setStroke(markerStroke);
        // Выбрать красный цвета для контуров маркеров
        canvas.setColor(Color.RED);
        // Выбрать красный цвет для закрашивания маркеров внутри
        canvas.setPaint(Color.RED);

        for (Double[] point : graphicsData) {
            Point2D.Double center = xyToPoint(point[0], point[1]);
            GeneralPath path = new GeneralPath();
            path.moveTo(center.x + 0, center.y + 5);
            path.lineTo(center.x - 1, center.y + 4);
            path.lineTo(center.x - 1, center.y + 2);
            path.lineTo(center.x - 2, center.y + 2);
            path.lineTo(center.x - 3, center.y + 1);
            path.lineTo(center.x - 4, center.y + 1);
            path.lineTo(center.x - 5, center.y + 0);
            path.lineTo(center.x - 4, center.y - 1);
            path.lineTo(center.x - 3, center.y - 1);
            path.lineTo(center.x - 2, center.y - 2);
            path.lineTo(center.x - 1, center.y - 2);
            path.lineTo(center.x - 1, center.y - 4);
            path.lineTo(center.x + 0, center.y - 5);
            path.lineTo(center.x + 1, center.y - 4);
            path.lineTo(center.x + 1, center.y - 2);
            path.lineTo(center.x + 2, center.y - 2);
            path.lineTo(center.x + 3, center.y - 1);
            path.lineTo(center.x + 4, center.y - 1);
            path.lineTo(center.x + 5, center.y + 0);
            path.lineTo(center.x + 4, center.y + 1);
            path.lineTo(center.x + 3, center.y + 1);
            path.lineTo(center.x + 2, center.y + 2);
            path.lineTo(center.x + 1, center.y + 2);
            path.lineTo(center.x + 1, center.y + 4);
            path.lineTo(center.x + 0, center.y + 5);
            canvas.draw(path);
        }
        // Шаг 2 - Организовать цикл по всем точкам графика
        for (Double[] point : graphicsData) {
            canvas.setPaint(Color.BLUE);

            if(isSumLessThanTen(point)) {
                Point2D.Double center = xyToPoint(point[0], point[1]);
                GeneralPath path = new GeneralPath();
                path.moveTo(center.x + 0, center.y + 5);
                path.lineTo(center.x - 1, center.y + 4);
                path.lineTo(center.x - 1, center.y + 2);
                path.lineTo(center.x - 2, center.y + 2);
                path.lineTo(center.x - 3, center.y + 1);
                path.lineTo(center.x - 4, center.y + 1);
                path.lineTo(center.x - 5, center.y + 0);
                path.lineTo(center.x - 4, center.y - 1);
                path.lineTo(center.x - 3, center.y - 1);
                path.lineTo(center.x - 2, center.y - 2);
                path.lineTo(center.x - 1, center.y - 2);
                path.lineTo(center.x - 1, center.y - 4);
                path.lineTo(center.x + 0, center.y - 5);
                path.lineTo(center.x + 1, center.y - 4);
                path.lineTo(center.x + 1, center.y - 2);
                path.lineTo(center.x + 2, center.y - 2);
                path.lineTo(center.x + 3, center.y - 1);
                path.lineTo(center.x + 4, center.y - 1);
                path.lineTo(center.x + 5, center.y + 0);
                path.lineTo(center.x + 4, center.y + 1);
                path.lineTo(center.x + 3, center.y + 1);
                path.lineTo(center.x + 2, center.y + 2);
                path.lineTo(center.x + 1, center.y + 2);
                path.lineTo(center.x + 1, center.y + 4);
                path.lineTo(center.x + 0, center.y + 5);
                canvas.draw(path);

            }
        }
    }

    // Метод, обеспечивающий отображение осей координат
    protected void paintAxis(Graphics2D canvas) {
        // Установить особое начертание для осей
        canvas.setStroke(axisStroke);
        // Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста - для получения характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();
        // Определить, должна ли быть видна ось Y на графике
        if (minX <= 0.0 && maxX >= 0.0) {
            // Она должна быть видна, если левая граница показываемой области (minX) <= 0.0,
            // а правая (maxX) >= 0.0
            // Сама ось - это линия между точками (0, maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            // Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести левый "скат" стрелки в точку с относительными координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            // Вести нижнюю часть стрелки в точку с относительными координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
            // Нарисовать подпись к оси Y
            // Определить, сколько места понадобится для надписи "y"
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));
        }
        // Определить, должна ли быть видна ось X на графике
        if (minY <= 0.0 && maxY >= 0.0) {
            // Она должна быть видна, если верхняя граница показываемой области (maxX) >= 0.0,
            // а нижняя (minY) <= 0.0
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                    xyToPoint(maxX, 0)));
            // Стрелка оси X
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // Вести верхний "скат" стрелки в точку с относительными координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20,
                    arrow.getCurrentPoint().getY() - 5);
            // Вести левую часть стрелки в точку с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY() + 10);
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
            // Нарисовать подпись к оси X
            // Определить, сколько места понадобится для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            // Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x", (float) (labelPos.getX() -
                    bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }
    }

    /* Метод-помощник, осуществляющий преобразование координат.
    * Оно необходимо, т.к. верхнему левому углу холста с координатами
    * (0.0, 0.0) соответствует точка графика с координатами (minX, maxY),
    где
    * minX - это самое "левое" значение X, а
    * maxY - самое "верхнее" значение Y.
    */
    protected Point2D.Double xyToPoint(double x, double y) {
        // Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
        // Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected Point2D.Double pointToXY(int x, int y) {
        Point2D.Double p = new Point2D.Double();
        if (!transform) {
            p.x = x / scale + minX;
            int q = (int) xyToPoint(0, 0).y;
            p.y = maxY - maxY * ((double) y / (double) q);
        } else {
            if (!zoom) {
                p.y = -x / scale + (maxY);
                p.x = -y / scale + maxX;
            } else {
                p.y = -x / scaleY + (maxY);
                p.x = -y / scaleX + maxX;
            }
        }
        return p;
    }

    public class MouseMotionHandler implements MouseMotionListener, MouseListener {
        private double comparePoint(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2)
                    + Math.pow(p1.y - p2.y, 2));
        }

        private GraphPoint find(int x, int y) {
            GraphPoint smp = new GraphPoint();
            GraphPoint smp2 = new GraphPoint();
            double r, r2 = 1000;
            for (int i = 0; i < graphicsData.length; i++) {
                Point p = new Point();
                p.x = x;
                p.y = y;
                Point p2 = new Point();
                p2.x = graphicsDataI[i][0];
                p2.y = graphicsDataI[i][1];
                r = comparePoint(p, p2);
                if (r < 7.0) {
                    smp.x = graphicsDataI[i][0];
                    smp.y = graphicsDataI[i][1];
                    smp.xd = graphicsData[i][0];
                    smp.yd = graphicsData[i][1];
                    smp.n = i;
                    if (r < r2) {
                        r2 = r;
                        smp2 = smp;
                    }
                    return smp2;
                }
            }
            return null;
        }

        public void mouseMoved(MouseEvent ev) {
            GraphPoint smp;
            smp = find(ev.getX(), ev.getY());
            if (smp != null) {
                setCursor(Cursor.getPredefinedCursor(8));
                SMP = smp;
            } else {
                setCursor(Cursor.getPredefinedCursor(0));
                SMP = null;
            }
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            if (selMode) {
                if (!transform)
                    rect.setFrame(mausePX, mausePY, e.getX() - rect.getX(),
                            e.getY() - rect.getY());
                else {
                    rect.setFrame(-mausePY + getHeight(), mausePX, -e.getY()
                            + mausePY, e.getX() - mausePX);
                }
                repaint();
            }
            if (dragMode) {
                if (!transform) {
                    if (pointToXY(e.getX(), e.getY()).y < maxY && pointToXY(e.getX(), e.getY()).y > minY) {
                        graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                        SMP.yd = pointToXY(e.getX(), e.getY()).y;
                        SMP.y = e.getY();
                    }
                } else {
                    if (pointToXY(e.getX(), e.getY()).y < maxY && pointToXY(e.getX(), e.getY()).y > minY) {
                        graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                        SMP.yd = pointToXY(e.getX(), e.getY()).y;
                        SMP.x = e.getX();
                    }
                }
                repaint();
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != 3)
                return;

            try {
                zone = stack.pop();
            } catch (EmptyStackException err) {

            }
            if (stack.empty())
                zoom = false;
            repaint();
        }

        public void mouseEntered(MouseEvent arg0) {

        }

        public void mouseExited(MouseEvent arg0) {

        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() != 1)
                return;
            if (SMP != null) {
                selMode = false;
                dragMode = true;
            } else {
                dragMode = false;
                selMode = true;
                mausePX = e.getX();
                mausePY = e.getY();
                if (!transform)
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
                else
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
            }
        }

        public void mouseReleased(MouseEvent e) {
            rect.setFrame(0, 0, 0, 0);
            if (e.getButton() != 1) {
                repaint();
                return;
            }
            if (selMode) {
                if (!transform) {
                    if (e.getX() <= mausePX || e.getY() <= mausePY)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY > getHeight())
                        eY = getHeight();
                    if (eX > getWidth())
                        eX = getWidth();
                    double MAXX = pointToXY(eX, 0).x;
                    double MINX = pointToXY(mausePX, 0).x;
                    double MAXY = pointToXY(0, mausePY).y;
                    double MINY = pointToXY(0, eY).y;
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXX = MAXX;
                    zone.MINX = MINX;
                    zone.MINY = MINY;
                    zone.MAXY = MAXY;
                    selMode = false;
                    zoom = true;
                } else {
                    if (pointToXY(mausePX, 0).y <= pointToXY(e.getX(), 0).y
                            || pointToXY(0, e.getY()).x <= pointToXY(0, mausePY).x)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY < 0)
                        eY = 0;
                    if (eX > getWidth())
                        eX = getWidth();
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXY = pointToXY(mausePX, 0).y;
                    zone.MAXX = pointToXY(0, eY).x;
                    zone.MINX = pointToXY(0, mausePY).x;
                    zone.MINY = pointToXY(eX, 0).y;
                    selMode = false;
                    zoom = true;
                }

            }
            repaint();
        }
    }
}
