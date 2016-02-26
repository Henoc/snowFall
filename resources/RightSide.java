package snowFall;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import static java.lang.Math.*;

public class RightSide {

	static int scoreAnim=ScheduledTask.score;			//scoreを表示のときにぴろぴろぴろ～ってするための変数

	static public void paintMachine(Graphics2D g){
		if(scoreAnim<ScheduledTask.score) scoreAnim+=min(5,abs(ScheduledTask.score-scoreAnim));
		if(scoreAnim>ScheduledTask.score) scoreAnim-=min(5,abs(ScheduledTask.score-scoreAnim));

		g.setColor(new Color(0,0,0,180));
		g.fillRect(Main.e_w, 0, Main.w-Main.e_w, Main.h);
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(Main.FONT_RIGHTMES);
		int height = g.getFontMetrics().getHeight();
		g.drawString("SCORE", 500, 200);
		g.drawString(""+scoreAnim, 600-g.getFontMetrics().stringWidth(""+scoreAnim), 200+height);
		g.drawString("LIFE", 500, 200+height*2);
		int life=ScheduledTask.life<0 ? 0 : ScheduledTask.life;			//表示のときは-1が出ないようにする
		g.drawString(""+life, 600-g.getFontMetrics().stringWidth(""+life), 200+height*3);
//		g.drawString("CLEARED "+ScheduledTask.numOfFallen, 500, 260);

	}
}
