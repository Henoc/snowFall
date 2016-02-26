package snowFall;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

//どの画面よりも前に書かなければいけないこと担当

public class GameOver {

	static final int pxOffset=20;

	static int cnt=0;

	String text;
	int px;
	int py;
	int width=0;
	int strwidth=0;
	int height=0;
	int ascent=0;
	int eveMode;

	//オブジェクトとしては、メニュー画面でのボタンを作る
	public GameOver(String text,int x,int y,int eventMode){
		this.text=text;
		px=x;
		py=y;
		eveMode=eventMode;
	}

	//ボタンの動作
	public void motionMachine(){
		if(isOverButton() && ScheduledTask.msLeft){		//クリック判定
			if(eveMode==1){
				Main.gameMode=1;
				ScheduledTask.cnt=-1;
				ScheduledTask.score=0;
				ScheduledTask.life=3;
				ScheduledTask.numOfSolved=0;
				ScheduledTask.numOfFallen=0;
				ScheduledTask.comboCnt=0;
				RightSide.scoreAnim=0;
			}
			if(eveMode==0){
				System.exit(0);
			}

			Main.wav[Main.WAV_BUTTON].start();
			Main.wav[Main.WAV_OPENING].stop();
			Main.wav[Main.WAV_GAMEOVER].stop();
//			Main.mid[Main.MID_CRYSTAL].play();		//BGM CRYSTALの再生
		}
	}


	private boolean isOverButton() {
		if(px-pxOffset<=ScheduledTask.msx && ScheduledTask.msx<px+pxOffset+width)
			if(py<=ScheduledTask.msy && ScheduledTask.msy<py+height)
				return true;
		return false;
	}

	//ゲームオーバー画面とタイトル画面を描く
	static public void paintMachine(Graphics2D g){
		if(Main.gameMode==0) paintTitle(g);
		if(Main.gameMode==2) paintGameOver(g);
	}

	static public void paintTitle(Graphics2D g){
		g.drawImage(Main.pic[Main.PIC_BG],0,0,Main.w,Main.h,null);
		g.setColor(new Color(0,0,0,140));
		g.fillRect(0, 0, Main.w, Main.h);
		g.drawImage(Main.pic[Main.PIC_TITLE],0,0,null);
	}
	static public void paintGameOver(Graphics2D g){
		g.setColor(new Color(0, 0, 0, cnt*4));
		g.fillRect(0, 0, Main.w, Main.h);

		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(Main.FONT_TITLE);
		FontMetrics metrics = g.getFontMetrics();
		g.drawString("GAME OVER", Main.w/2-metrics.stringWidth("GAME OVER")/2, Main.h/2-metrics.getHeight()/2);
		g.drawString("HISCORE "+ScheduledTask.hiscore, Main.w/2-metrics.stringWidth("HISCORE "+ScheduledTask.hiscore)/2, Main.h/2+metrics.getHeight()/2);

		cnt=Math.min(cnt+1, 50);
	}

	//オブジェクトを描画
	public void paintButtonMachine(Graphics2D g){
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(Main.FONT_TITLE);
		if(width==0){		//文字の幅、高さがわかるので入れておいて、ボタンの当たり判定に使う
			FontMetrics metrics = g.getFontMetrics();
			strwidth=metrics.stringWidth(text);
			width=strwidth<80 ? 80 : strwidth;
			height=metrics.getHeight();
			ascent=metrics.getAscent();
		}

		g.drawString(text, px, py+ascent);
		if(isOverButton()){
			g.drawRect(px-pxOffset, py,width+pxOffset*2,height);
		}

	}
}
