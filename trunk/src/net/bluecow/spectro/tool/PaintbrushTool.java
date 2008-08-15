/*
 * Created on Jul 29, 2008
 *
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package net.bluecow.spectro.tool;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.Clip;
import net.bluecow.spectro.ClipPanel;
import net.bluecow.spectro.Frame;

/**
 * Tool for painting out a region of the spectral data based on mouse
 * press-and-drag. This was the original tool in Spectro-Edit.
 */
public class PaintbrushTool implements Tool {

    private ClipPanel clipPanel;
    private Clip clip;
    private final PaintbrushMouseHandler mouseHandler = new PaintbrushMouseHandler();
    
    // settings panel stuff
    private final JPanel settingsPanel;
    private final JSlider brushSlider;
    private final JLabel brushSizeLabel;
    
    /**
     * Creates a new paintbrush tool. To associate it with a specific ClipPanel,
     * activate it with {@link #activate(ClipPanel)}.
     */
    public PaintbrushTool() {
        settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(brushSlider = new JSlider(1, 20, 1));
        settingsPanel.add(brushSizeLabel = new JLabel());
        brushSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                brushSizeLabel.setText("Paintbrush size: " + brushSlider.getValue());
            }
        });
        brushSlider.setValue(5);
    }
    
    public void activate(ClipPanel cp) {
        clipPanel = cp;
        clip = clipPanel.getClip();
        clipPanel.addMouseListener(mouseHandler);
        clipPanel.addMouseMotionListener(mouseHandler);
    }

    public void deactivate() {
        clipPanel.removeMouseListener(mouseHandler);
        clipPanel.removeMouseMotionListener(mouseHandler);
        clip = null;
        clipPanel = null;
    }
    
    private class PaintbrushMouseHandler implements MouseMotionListener, MouseListener {

        public void mouseDragged(MouseEvent e) {
            int radius = brushSlider.getValue();
            Point p = clipPanel.toClipCoords(e.getPoint());
            for (int x = p.x - radius; x < p.x + radius; x++) {
                Frame f = clip.getFrame(x);
                for (int y = p.y - radius; y < p.y + radius; y++) {
                    f.setReal(y, 0.0);
                }
            }
            Rectangle updateRegion = new Rectangle(e.getX() - radius, e.getY() - radius, radius * 2, radius * 2);
            clipPanel.updateImage(updateRegion);
            clipPanel.repaint(e.getX() - radius, e.getY() - radius, radius * 2, radius * 2);
//          clipPanel.repaint(updateRegion);
//            clipPanel.repaint(p.x, p.y, radius * 2, radius * 2);
        }

        public void mouseMoved(MouseEvent e) {
            // maybe draw an outline of the paintbrush
        }

        public void mouseClicked(MouseEvent e) {
            // don't care?
        }

        public void mouseEntered(MouseEvent e) {
            // don't care?
        }

        public void mouseExited(MouseEvent e) {
            // don't care?
        }

        public void mousePressed(MouseEvent e) {
            mouseDragged(e);
        }

        public void mouseReleased(MouseEvent e) {
            // don't care?
        }
        
    }

    public JComponent getSettingsPanel() {
        return settingsPanel;
    }
    
    @Override
    public String toString() {
        return "Paintbrush";
    }
}