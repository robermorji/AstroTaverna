package org.purl.wf4ever.astrotaverna.vo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.event.CaretListener;

import uk.ac.starlink.vo.TapTableLoadDialog;
import uk.ac.starlink.vo.TapQueryPanel;

/**
 * Load dialogue for AstroTaverna TAP services.
 *
 * @author   Mara Jímenez
 * @since    11 Nov 2013
 * @see <a href="http://www.ivoa.net/Documents/TAP/">IVOA TAP Recommendation</a>
 */
public class AstroTapTableLoadDialog extends TapTableLoadDialog {
	
	private JComponent tqContainer_;
    private TapQueryPanel tqPanel_;
    private CaretListener adqlListener_;
    private final Map<String,TapQueryPanel> tqMap_;
	
	
    /**
     * Constructor.
     */
    public AstroTapTableLoadDialog() {
        super();
        tqMap_ = new HashMap<String,TapQueryPanel>();
        setIconUrl( TapTableLoadDialog.class.getResource( "tap.gif" ) );
    }    
    
	/**
     * Configure this dialogue to use a TAP service with a given service URL.
     * Adapted for AstroTaverna
     *
     * @param  serviceUrl  service URL for TAP service
     */
    public void setSelectedService( String serviceUrl, JPanel tablePanel ) {

        /* We have to install a TapQueryPanel for this service in the 
         * appropriate tab of the tabbed pane.
         * First remove any previously installed query panel. */
    	if ( tablePanel != null){
    		tqContainer_ = tablePanel;
    	}
    	
        if ( tqPanel_ != null ) {
            tqContainer_.remove( tqPanel_ );
            tqPanel_.getAdqlPanel().removeCaretListener( adqlListener_ );
            tqPanel_ = null;
        }
        if ( serviceUrl != null ) {

            /* Construct, configure and cache a suitable query panel
             * if we haven't seen this service URL before now. */
            if ( ! tqMap_.containsKey( serviceUrl ) ) {
                TapQueryPanel tqPanel = createTapQueryPanel();
                tqPanel.setServiceHeading( serviceUrl );
                tqPanel.setServiceUrl( serviceUrl );
                tqMap_.put( serviceUrl, tqPanel );
            }

            /* Get the panel from the cache, now guaranteed present. */
            tqPanel_ = tqMap_.get( serviceUrl );

            /* Install ready for use. */
            tqPanel_.getAdqlPanel().addCaretListener( adqlListener_ );
            tqContainer_.add( tqPanel_, BorderLayout.CENTER );
            
        }
      // updateReady();
       
      // tqContainer_.repaint();
      //tqContainer_.revalidate();
    }	
    
    public Component getConmponent(){
    	return tqContainer_;
    }
	
}