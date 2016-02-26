package snowFall;

import java.awt.Graphics2D;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import static java.lang.Math.*;

public class Combo {

	//このクラスのオブジェクトを入れておく
	static ArrayList<Combo> comboList = new ArrayList<Combo>();

	int comboCnt;
	double x;
	double y;
	static final int CNT_MAX = 50;
	int cnt = CNT_MAX;
	boolean deleteFlag=false;

	public Combo(int comboCnt){
		this.comboCnt=comboCnt;
		x=Main.e_w/2;
		y=Main.h/2;
	}

	public void paintMachine(Graphics2D g){
		float alpha = (float)sqrt((double)cnt/CNT_MAX);


		//コンボの回数によって色変え
		float br=1,bg=1,bb=1;
		if(range(10,comboCnt,20)){
			br = 0.45f;
			bg = 1.25f;
		}
		if(range(20,comboCnt,50)){
			br = 1.40f;
			bb = 0.85f;
		}
		if(range(50,comboCnt,100)){
			bb = 1.35f;
			bg = 1.25f;
			br = 0.75f;
		}
		if(range(100,comboCnt,200)){
			br = 1.5f;
			bg = 1.5f;
			bb = 1.5f;
		}
		if(range(200,comboCnt,500)){
			br = 0.5f;
			bg = 1.5f;
			bb = 0.5f;
		}
		if(comboCnt>=500){
			br = 2.0f;
			bg = 2.0f;
			bb = 2.0f;
		}
		RescaleOp op = new RescaleOp(new float[]{br,bg,bb,alpha}, new float[]{0,0,0,0}, null);
		g.drawImage(Main.pic[Main.PIC_COMBO],op,(int)x,(int)y);
		int comboHeight = Main.pic[Main.PIC_COMBO].getHeight();
		int numX=(int)x,numY=(int)y;
		for(int i=0;i<(""+comboCnt).length();i++){
			int num=comboCnt/(int)pow(10,i)%10;
			numX-=Main.pic[Main.PIC_NUMBERS10+num].getWidth();
			g.drawImage(Main.pic[Main.PIC_NUMBERS10+num],op,numX,numY-(Main.pic[Main.PIC_NUMBERS10+num].getHeight()-comboHeight));
		}
		y-=0.5;
		//削除対象にする
		cnt--;
		if(cnt==0) deleteFlag=true;
	}

	//range関数
	public boolean range(int bottom,int n,int upper){
		return bottom<=n && n<upper;
	}
}
