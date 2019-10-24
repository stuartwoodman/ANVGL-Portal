package org.auscope.portal.server.web.controllers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.server.controllers.BasePortalController;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Parse GRACE solution and mascon files and return a series of mascon values
 * for lat/lon points.
 * 
 * @author woo392
 *
 */
@Controller
public class GraceVisualisationController extends BasePortalController {

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 
	 * @param solutionFileUrl
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Map<Integer, Double> getMasconValues(String solutionFileUrl) throws FileNotFoundException, IOException {
		Map<Integer, Double> masconValues = new HashMap<Integer, Double>();
		BufferedReader solutionReader = null;
		String line = "";

		try {
			// Parse solution file for mascon values
			solutionReader = new BufferedReader(new FileReader(solutionFileUrl));
			while ((line = solutionReader.readLine()) != null) {
				String[] columns = line.trim().split(" +");
				// Look for first column "<integer>." and second column "MC<integer>"
				if (columns.length == 7 && columns[0].matches("^\\d+\\.") && columns[1].matches("^MC\\d+")) {
					// Convert to integer to remove inconsistent number of initial zeroes in code
					int masconCode = Integer.valueOf(columns[1].substring(2, columns[1].length()));
					double masconValue = Double.valueOf(columns[5]);
					masconValues.put(masconCode, masconValue);
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (solutionReader != null) {
				try {
					solutionReader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return masconValues;
	}

	/**
	 * TODO: Get rid of if not used, if we're using Geotools just build
	 * SimpleFeature list
	 * 
	 * @param masconValues
	 * @param masconFileUrl
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	/*
	private List<SimplePosition> getTernaryMasconValues(Map<Integer, Double> masconValues, String masconFileUrl)
			throws FileNotFoundException, IOException {
		List<SimplePosition> ternaryMasconValues = new ArrayList<SimplePosition>();
		BufferedReader masconReader = null;
		String line = "";
		try {
			// Parse mascon file for ternary mascon positions
			masconReader = new BufferedReader(new FileReader(masconFileUrl));
			while ((line = masconReader.readLine()) != null) {
				String[] columns = line.trim().split(" +");
				if (columns.length == 14 && !columns[0].startsWith("#")) {
					try {
						// Assumes mascon code will always be of the form "<integer>."
						Integer masconCode = Integer.valueOf(columns[8].substring(0, columns[8].length() - 1));
						if (masconValues.containsKey(masconCode)) {
							Double lon = Double.valueOf(columns[3]);
							Double lat = Double.valueOf(columns[2]);
							Double val = masconValues.get(masconCode);
							ternaryMasconValues.add(new SimplePosition(lon, lat, val));
						}
					} catch (NumberFormatException e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (masconReader != null) {
				try {
					masconReader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return ternaryMasconValues;
	}
	*/

	private Color getColor(double value) {
		// Need to normalize values
		/*
		 * double h = value * 0.4; double s = 0.9; double b = 0.9; return
		 * Color.getHSBColor((float)h, (float)s, (float)b);
		 */
		/*
		 * double r = (255 * value) / 100; double g = (255 * (100 - value)) / 100;
		 * double b = 0; return new Color((float)r, (float)g, (float)b);
		 */
		return Color.black;
	}

	/*
	private byte[] createImageByteArray(List<SimplePosition> positionList) throws IOException {
		byte[] byteArray = null;
		try {
			int imagePixelWidth = 3000;
			// int bubble_size = 5;
			BufferedImage image = new BufferedImage(imagePixelWidth, imagePixelWidth, BufferedImage.TYPE_INT_ARGB);

			double minLat = -90.0;
			double minLon = 0.0;
			double maxLat = 90.0;
			double maxLon = 360.0;

			Graphics2D graphics = (Graphics2D) image.getGraphics();

			double latExtent = maxLat - minLat;
			double lonExtent = maxLon - minLon;

			for (SimplePosition p : positionList) {
				graphics.setColor(getColor(p.value));
				double ly1 = (imagePixelWidth * (p.lat - minLat)) / latExtent;
				double lx1 = (imagePixelWidth * (p.lon - minLon)) / lonExtent;
				int ly = (int) (imagePixelWidth - ly1); // pixel increases downwards. Latitude increases upwards (north
														// direction). So you need to inverse your mapping.
				int lx = (int) lx1;

				graphics.drawLine(lx, ly, lx, ly); // Just a point
				// Could display values using bubble size
				// graphics.fillOval(lx - bubble_size / 2, ly - bubble_size / 2,
				// bubble_size, bubble_size);
			}
			// ImageIO.write(image, "png", new
			// File("/Users/woo392/dev/workspaces/GRACE/data/test.png"));

			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bao);
			byteArray = bao.toByteArray();

		} catch (IOException e) {
			throw e;
		}
		return byteArray;
	}
	*/

	/**
	 * 
	 * @param masconValues
	 * @param masconFileUrl
	 * @return
	 */
	private List<SimpleFeature> createFeaturesFromTernaryMasconValues(Map<Integer, Double> masconValues,
			String masconFileUrl) throws FileNotFoundException, IOException {
		List<SimpleFeature> features = new ArrayList<>();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		BufferedReader masconReader = null;
		String line = "";
		try {
			// Parse mascon file for ternary mascon positions
			masconReader = new BufferedReader(new FileReader(masconFileUrl));
			while ((line = masconReader.readLine()) != null) {
				String[] columns = line.trim().split(" +");
				if (columns.length == 14 && !columns[0].startsWith("#")) {
					try {
						// Assumes mascon code will always be of the form "<integer>."
						Integer masconCode = Integer.valueOf(columns[8].substring(0, columns[8].length() - 1));
						if (masconValues.containsKey(masconCode)) {
							// LAt/Long/Value from ternary mascon file
							double lon = Double.parseDouble(columns[3]);
							if(lon > 180)
								lon -= 360;
							double lat = Double.parseDouble(columns[2]);
							double value = masconValues.get(masconCode).doubleValue();
							// Create feature
							Point p = geometryFactory.createPoint(new Coordinate(lon, lat));
							featureBuilder.add(p);
							// featureBuilder.add(name);
							featureBuilder.add(value);
							SimpleFeature feature = featureBuilder.buildFeature(null);
							features.add(feature);
						}
					} catch (NumberFormatException e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (masconReader != null) {
				try {
					masconReader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return features;
	}

	private static SimpleFeatureType createFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Location");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("the_geom", Point.class);
		builder.length(15).add("Name", String.class);
		builder.add("number", Integer.class);
		final SimpleFeatureType LOCATION = builder.buildFeatureType();

		return LOCATION;
	}

	/**
	 * TODO: currently returns null, make work with new methods
	 * TODO: What to return on failure?
	 * 
	 * @param solutionFileUrl
	 * @param masconFileUrl
	 * @return
	 */
	@RequestMapping("/getGraceVisualisation.do")
	public @ResponseBody byte[] graceVisualisation(@RequestParam("solutionFileUrl") final String solutionFileUrl,
			@RequestParam("masconFileUrl") final String masconFileUrl) {
		//List<SimplePosition> ternaryValues = new ArrayList<SimplePosition>();
		byte[] imageByteArray = null;
		try {
			Map<Integer, Double> masconValues = getMasconValues(solutionFileUrl);
			//ternaryValues = getTernaryMasconValues(masconValues, masconFileUrl);
			// imageByteArray = createImageByteArray(ternaryValues);
		} catch (FileNotFoundException e) {
			// TODO: ?
		} catch (IOException e) {
			// TODO: ?
		}
		return imageByteArray;
	}

	/**
	 * TODO: Remove this, was useful when returning JSON, but number of values makes
	 * that infeasible.
	 * 
	 * Simple class for serialising a latitude/longitude with an associated value.
	 * 
	 * @author woo392
	 *
	 */
	/*
	private class SimplePosition implements Serializable {

		private static final long serialVersionUID = 118194744245287719L;

		public Double lon;
		public Double lat;
		public Double value;

		public SimplePosition(Double lon, Double lat, Double value) {
			this.lon = lon;
			this.lat = lat;
			this.value = value;
		}

	}
	*/
	
	private void createGraceVisualization(final String solutionFileUrl, final String masconFileUrl) throws Exception {
		// Map
		MapContent map = new MapContent();
		
		// Mascon feature layer
		Map<Integer, Double> masconValues = getMasconValues(solutionFileUrl);
		List<SimpleFeature> features = createFeaturesFromTernaryMasconValues(masconValues, masconFileUrl);
		SimpleFeatureCollection featureCollection = new ListFeatureCollection(createFeatureType(), features);
		Style featureStyle = SLD.createPointStyle("Circle", Color.RED, Color.RED, 1.0f, 1.0f);
		Layer featureLayer = new FeatureLayer(featureCollection, featureStyle);
		map.addLayer(featureLayer);
		
		// World Map shapefile as base map
		File worldMapFile = new File("/Users/woo392/gis/Countries_WGS84/Countries_WGS84.shp");
		Map<String, Object> fileMap = new HashMap<>();
        fileMap.put("url", worldMapFile.toURI().toURL());
		DataStore dataStore = DataStoreFinder.getDataStore(fileMap);
		String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> worldMapSource = dataStore.getFeatureSource(typeName);
        Style worldMapStyle = SLD.createPolygonStyle(Color.BLACK, null, 0.0f);
        Layer worldMapLayer = new FeatureLayer(worldMapSource, worldMapStyle);
        map.addLayer(worldMapLayer);
		
        // JFrame for testing
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		GTRenderer renderer = new StreamingRenderer();
		JFrame frame = new JFrame("GraceVizTest");
		frame.setLayout(new BorderLayout());
		JMapPane mapPane = new JMapPane();
		mapPane.setRenderer(renderer);
		mapPane.setMapContent(map);
		frame.add(mapPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setSize(1800, 1200);
	    frame.setVisible(true);
	}

	public static void main(String[] args) {
		GraceVisualisationController gvc = new GraceVisualisationController();
		try {
			gvc.createGraceVisualization("/Users/woo392/dev/workspaces/GRACE/data/2019_07.fit",
				"/Users/woo392/dev/workspaces/GRACE/data/mascons_stage4_V002");
		} catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
