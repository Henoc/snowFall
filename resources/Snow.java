package snowFall;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import static java.lang.Math.*;

public class Snow {

	double x,y;			//中心(x,y)
	double fspeed;		//落下速度[/frame]
	int num;			//snowListに入る番号
	SnowMatrix parent;	//このオブジェトの所属するSnowMatrix
	boolean status;
	int bonus;			//=1なら1upつき
	int anim;
	int life=50;		//爆発するまでのカウント
	static final int width=66,height=66,radius=20;
	static ArrayList<Snow> allSnowList = new ArrayList<Snow>();		//すべての生成されたSnowオブジェクトを入れておく
	static Snow SnowMouseOvered = null;

	public Snow(int num,SnowMatrix par,int centX,int centY,double fsp){
		this.num=num;
		parent=par;
		x=centX;
		y=centY;
		fspeed=fsp;
		status=false;
		anim = status? 0 : 6;
		par.bonus[num]=bonus= ScheduledTask.rnd.nextInt(60)==0 ? 1 : 0;
		allSnowList.add(this);
	}

	public void paintMachine(Graphics2D g){
		float alpha =(float)parent.vanishAnim/SnowMatrix.VANISH_ANIM_MAX;
		RescaleOp op = new RescaleOp(new float[]{1,1,1,alpha}, new float[]{0,0,0,0}, null);

		if(anim==0) g.drawImage(Main.pic[Main.PIC_SNOW],op,(int)x-width/2,(int)y-height/2);
		if(anim==6) g.drawImage(Main.pic[Main.PIC_CRYSTAL],op,(int)x-width/2,(int)y-height/2);
		if(anim>=1 && anim<=5) g.drawImage(Main.pic[Main.PIC_SNOW_ANIM5+anim-1],op,(int)x-width/2,(int)y-height/2);

		//ボーナス用op
		float br=1,bg=1,bb=1;
		if(parent.vanishAnim!=SnowMatrix.VANISH_ANIM_MAX){
			br = parent.vanishAnim%5*0.25f;
			bg = (parent.vanishAnim+1)%5*0.25f;
			bb = (parent.vanishAnim+2)%5*0.25f;
		}
		op = new RescaleOp(new float[]{br,bg,bb,(float)sqrt(alpha)}, new float[]{0,0,0,0}, null);

		//ボーナス
		if(bonus==1) g.drawImage(Main.pic[Main.PIC_1UP],op,(int)x-Main.pic[Main.PIC_1UP].getWidth()/2,(int)y-Main.pic[Main.PIC_1UP].getHeight()/2);
//		g.fillOval((int)x-width/2, (int)y-height/2, width, height);
		if(snowFieldCheck()){
			g.setColor(Color.ORANGE);
			g.drawOval((int)x-width/2, (int)y-height/2, width, height);
		}
	}

	public void motionMachine(){			//Snowオブジェクトの動きを記述
		if(status && anim>0) anim--;
		if(!status && anim<6) anim++;
		y+=fspeed;
		if(Main.h<y) y=Main.h;
		if(Main.h==y){
			if(life>0) life--;
		}
	}

	public static void snowFieldCheckInit(){		//snowFieldCheckのための初期化
		SnowMouseOvered=null;
		if(Main.gameMode==2) return;		//ゲームオーバー時なら判定しない
		for(Snow s : allSnowList){
			if(sqrt(pow(ScheduledTask.msx-s.x,2)+pow(ScheduledTask.msy-s.y,2))<radius){
				SnowMouseOvered=s;
				break;
			}
		}
	}

	public boolean snowFieldCheck(){			//Snowオブジェクトがカーソル上かはここで判定する
		if(this==SnowMouseOvered)
			return true;
		return false;
	}
}
