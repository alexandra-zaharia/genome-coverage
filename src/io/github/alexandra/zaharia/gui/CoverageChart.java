package io.github.alexandra.zaharia.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import io.github.alexandra.zaharia.search.GenomeCoverage;


/**
 * La classe <code>CoverageChart</code> permet de réaliser des courbes de
 * couverture pour un ou plusieurs génomes. Elle utilise la bibliothèque
 * graphique <i>open source</i> 
 * <a href="http://www.jfree.org/jfreechart"><code>JFreeChart</code></a>
 * basée sur <code>Java2D</code>.
 */
public class CoverageChart {
    /**
     * Le <i>dataset</i> (les données) pour le graphique.
     */
    private XYDataset dataset;
    
    /**
     * Le panneau (composant graphique) contenant la courbe de couverture.
     */
    private ChartPanel chartPanel;
    
    
    /**
     * Constructeur de la classe.
     * <p>
     * Permet de créer la courbe de couverture pour un seul génome.
     * 
     * @param gc référence vers l'objet de type {@link GenomeCoverage}
     * contenant les informations de couverture pour un seul génome
     */
    public CoverageChart(GenomeCoverage gc) {
        dataset = createDataset(gc);
        chartPanel = doChartPanel(gc.getGenomeId());
    }
    
    
    /**
     * Constructeur de la classe.
     * <p>
     * Permet de créer des courbes de couverture pour plusieurs génomes.
     * 
     * @param gc tableau d'objets de type {@link GenomeCoverage} 
     * contenant les informations de couverture pour plusieurs génomes
     */
    public CoverageChart(GenomeCoverage[] gc) {
        dataset = createDataset(gc);
        chartPanel = doChartPanel(null);
    }
    
    
    /**
     * Renvoie le panneau contenant la ou les courbes de couverture généré
     * par la méthode {@code doChartPanel}.
     * 
     * @return {@code chartPanel} contenant la ou les courbes de couverture
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
    
    
    /**
     * Crée le <i>dataset</i> à utiliser pour la construction de la courbe
     * de couverture pour un seul génome. 
     * 
     * @param gc référence vers l'objet de type {@code GenomeCoverage} 
     * utilisé pour générer la courbe de couverture
     * 
     * @return le <i>dataset</i> contenant les inforamtions de couverture
     * spécifiées par l'objet {@code gc}
     */
    private XYDataset createDataset(GenomeCoverage gc) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Génome");
        int[] coverage = gc.getCoverage();
        for (int i = 0; i < gc.length(); i++) series.add(i, coverage[i]);        
        dataset.addSeries(series);
        return dataset;
    }
    
    
    /**
     * Crée le <i>dataset</i> à utiliser pour la construction des courbes
     * de couverture pour plusieurs génomes.
     * 
     * @param gc tableau d'objets de type {@code GenomeCoverage} utilisés
     * pour générer courbe de couverture
     * 
     * @return le <i>dataset</i> contenant les inforamtions de couverture
     * spécifiées par le tableau {@code gc}
     */
    private XYDataset createDataset(GenomeCoverage[] gc) {
        /* Dirty fix pour aligner les items dans la légende correctement:
         * padding des identifiants les plus courts avec des espaces... On
         * détermine d'abord la longueur du plus long identifiant.
         */
        int maxLength = gc[0].getGenomeId().length();
        for (int i = 1; i < gc.length; i++)
            if (maxLength < gc[i].getGenomeId().length())
                maxLength = gc[i].getGenomeId().length();
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (GenomeCoverage g : gc) {
            int[] coverage = g.getCoverage();
            String title = g.getGenomeId();
            /* On fait un padding avec des espaces pour les identifiants 
             * plus courts que maxLength.
             */
            if (title.length() < maxLength)
                for (int i = 0; i < maxLength - g.getGenomeId().length(); i++)
                    title = title + " ";
            XYSeries series = new XYSeries(title);
            for (int i = 0; i < g.length(); i++) series.add(i, coverage[i]);
            dataset.addSeries(series);
        }
        
        return dataset;
    }
    
    
    /**
     * Crée le {@code ChartPanel} contenant le graphique.
     * 
     * @param subtitle chaîne de caractères donnant le soustitre à afficher sur
     * le graphique; peut être null (s'il s'agit d'un graphique contenant les
     * courbes de couverture pour plusieurs génomes)
     * 
     * @return le {@code ChartPanel} contenant la ou les courbes de couverture
     */
    public ChartPanel doChartPanel(String subtitle) {
        JFreeChart chart = createChart(dataset, subtitle);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);
        chartPanel.setDomainZoomable(false);
        chartPanel.setMouseZoomable(false);
        chartPanel.setRangeZoomable(false);
        return chartPanel;
    }


    /**
     * Crée le {@code JFreeChart} affichant la ou les courbes de couvertures.
     * 
     * @param dataset les données à utiliser pour la construction du graphique
     * 
     * @param subtitle chaîne de caractères donnant le soustitre à utiliser;
     * peut être null
     * 
     * @return le {@code JFreeChart} affichant la ou les courbes de couverture
     */
    private static JFreeChart createChart(XYDataset dataset, String subtitle) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Courbe de couverture",                       // titre
            "Position dans le génome",                    // label sur l'axe X
            "Nombre de séquences",                        // label sur l'axe Y
            dataset,                                      // données
            PlotOrientation.VERTICAL,                     // orientation
            dataset.getSeriesCount() != 1,                // légende
            true,                                         // tooltips
            false                                         // URLs
        );
        
        if (subtitle != null)
            chart.addSubtitle(new TextTitle("Génome " + subtitle));
        
        chart.setBackgroundPaint(Color.lightGray);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.black);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        /* On a besoin d'un renderer pour faire quelques ajustements au
         * graphique de base.
         */
        XYLineAndShapeRenderer renderer = new LegendRenderer();
        renderer.setDrawOutlines(true);
        renderer.setBaseShapesVisible(false);
        renderer.setBaseStroke(new BasicStroke(1.0f));
        if (dataset.getSeriesCount() == 1)
            renderer.setSeriesPaint(0, GUIModel.ORANGE);
        plot.setRenderer(renderer);        
        
        /* Si le graphique affiche plusieurs courbes de couverture, il faut
         * ajuster la légende.
         */
        if (dataset.getSeriesCount() > 1) {
            LegendTitle legend = chart.getLegend();
            legend.setBackgroundPaint(Color.darkGray);
            legend.setItemFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            legend.setItemPaint(Color.WHITE);
            legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
            legend.setPadding(5, 5, 5, 5);
        }
        
        return chart;
    }
    
    
    /**
     * Classe interne statique permettant de personnaliser le graphique
     * et sa légende.
     *
     */
    private static class LegendRenderer extends XYLineAndShapeRenderer {
        private static final long serialVersionUID = 1L;

        /**
         * Méthode rédéfinie permettant d'afficher les lignes dans la légende
         * avec un trait plus épais.
         * 
         * @param dataset le <i>dataset</i> à utiliser
         *
         * @param series l'indice de la série dans le <i>dataset</i> à
         * considérer
         *
         * @return référence vers l'objet de type {@code LegendItem} dont les 
         * caractéristiques sont ajustées par cette méthode
         */
        public LegendItem getLegendItem(int dataset, int series) {
            LegendItem legend = super.getLegendItem(dataset, series);
            return new LegendItem(
                legend.getLabel(),
                legend.getDescription(), 
                legend.getToolTipText(), 
                legend.getURLText(), 
                new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                new BasicStroke(2.0f),
                legend.getFillPaint()
            ); 
        }
    }
}
