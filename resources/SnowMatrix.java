package snowFall;

import static java.lang.Math.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

public class SnowMatrix {

	boolean[][] matrix;			//グラフの隣接行列
	Snow[] snowList;
	int vmax,complex,edgeForm;
	int status;
	int[] bonus;				//このMatrixのSnowにボーナスが付いていたらこれにもつける
	static final int VANISH_ANIM_MAX=25;
	int vanishAnim=VANISH_ANIM_MAX;

	//densityはグラフの辺の多さ　0でなし 100で全部
	public SnowMatrix(Random rnd,int vmax,int complex,int[] posx,int[] posy,double fspeed, int density,int edgeForm){

		bonus = new int[vmax];
		for(int i=0;i<vmax;i++)
			bonus[i]=0;			//ここでは初期値0を入れておく
		this.status=0;
		this.vmax=vmax;
		this.complex=complex;
		this.edgeForm=edgeForm;

		//グラフのつながり型を任意に決定
		matrix=new boolean[vmax][vmax];
		if(edgeForm==0){
			for(int i=0;i<vmax;i++)
				for(int j=i+1;j<vmax;j++)
					matrix[j][i]=matrix[i][j]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
		}
		if(edgeForm==1){		//順番につなぐのみ
			for(int i=0;i<vmax-1;i++)
				matrix[i][i+1]=matrix[i+1][i]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
		}
		//edgeForm==2なら繋がない
		if(edgeForm==3){		//格子状に繋ぐ　FORM_LATTICE専用
			for(int i=0;i<vmax-1;i++)
				matrix[i][i+1]=matrix[i+1][i]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
			matrix[3][8]=matrix[8][3]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
			matrix[0][7]=matrix[7][0]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
			matrix[1][8]=matrix[8][1]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
			matrix[5][8]=matrix[8][5]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
		}
		if(edgeForm==4){
			for(int i=0;i<vmax;i++)
				matrix[i][(i+1)%vmax]=matrix[(i+1)%vmax][i]= density==0 ? false : rnd.nextDouble()*100/density<1.0;
		}

		snowList = new Snow[vmax];

		for(int i=0;i<vmax;i++){
			snowList[i]=new Snow(i,this,posx[i],posy[i]-120,fspeed);
		}

		//任意にいじっておく
		for(int k=0;k<5;k++){

			boolean[] isAlreadyClicked = new boolean[vmax];
			for(int i=0;i<complex;){
				int f=0;
				for(int j=0;j<vmax;j++)
					if(isAlreadyClicked[j]) f++;
				if(f==vmax) break;				//すべての頂点をいじったならこれ以上複雑にできないので終わりにする

				int a = rnd.nextInt(vmax);
				if(!isAlreadyClicked[a]){
					isAlreadyClicked[a]=true;
					clickAction(a);
					i++;
				}
			}

			if(!isComplete(this)) break;		//SnowMatrixが未完成ならそれを出す
			//いじった後でもSnowMatrixが完成しているならいじりミスなのでやり直す、ただ何度やっても完成品しかできないパターンがあるかもなので適当に５回くらい試す
		}
	}

	public void motionMachine(){

		for(Snow s : snowList){			//すべてのSnowオブジェクトについて、計算処理をする
			if(s.snowFieldCheck() && ScheduledTask.msLeft){
				clickAction(s.num);//クリックされたときのモーション
				Main.wav[Main.WAV_CHANGE].start();
			}
			s.motionMachine();
		}

		fallenSnowCheck(snowList);		//このSnowMatrixはすべて落下済みか？チェック
		if(isComplete(this) && vanishAnim==VANISH_ANIM_MAX){			//このSnowMatrixは完成しているか
			status=-1;			//status=-1のSnowMatrixはScheduledTaskにより削除される
			for(int i=0;i<vmax;i++)					//ボーナス確定
				if(bonus[i]==1) {
					ScheduledTask.life++;
					Main.wav[Main.WAV_1UP].start();
				}
			ScheduledTask.score+=vmax*10+complex*50+ScheduledTask.comboCnt*5;				//消えたのでスコアに反映
			ScheduledTask.numOfSolved++;
			ScheduledTask.comboCnt++;
			if(ScheduledTask.comboCnt>=2)
				Combo.comboList.add(new Combo(ScheduledTask.comboCnt));

			Main.wav[Main.WAV_GOOD].start();		//効果音再生
		}
	}

	public void fallenSnowCheck(Snow[] list){
		int checker=0;
		for(int i=0;i<list.length;i++){
			if(list[i].life==0) checker++;
		}
		if(checker==list.length && vanishAnim==VANISH_ANIM_MAX){
			status=-1;
			if(ScheduledTask.life>0) ScheduledTask.life--;
			ScheduledTask.comboCnt=0;
			Main.wav[Main.WAV_MISS].start();
		}
	}

	public boolean isComplete(SnowMatrix sm){		//SnowMatrixが完成されているかどうか判定
		for(Snow s : snowList)
			if(s.status) return false;
		return true;
	}

	public void clickAction(int v){
		for(int i=0;i<vmax;i++){
			if(matrix[v][i] || i==v)		//もし頂点vとiがつながっているなら、その頂点のSnowオブジェクトの状態を反転
				snowList[i].status=!snowList[i].status;
		}
	}

	public void paintMachine(Graphics2D g){

		if(edgeForm==0){
			for(int i=0;i<vmax;i++)
				for(int j=i+1;j<vmax;j++){
					drawEdge(i,j,g);
				}
		}
		if(edgeForm==1){
			for(int i=0;i<vmax-1;i++){
				drawEdge(i,i+1,g);
			}
		}
		//edgeForm==2なら繋がない
		if(edgeForm==3){
			for(int i=0;i<vmax-1;i++)
				drawEdge(i,i+1,g);
			drawEdge(3,8,g);
			drawEdge(0,7,g);
			drawEdge(1,8,g);
			drawEdge(5,8,g);
		}
		if(edgeForm==4){
			for(int i=0;i<vmax;i++){
				drawEdge(i,(i+1)%vmax,g);
			}
		}

		for(Snow s : snowList)
			s.paintMachine(g);
	}

	//辺を繋ぐ画像処理、paintMachineから使う
	private void drawEdge(int i,int j,Graphics2D g){
//		int size=Main.pic[Main.PIC_LINE_ON].getHeight();
//		double distance = sqrt(pow(snowList[i].x-snowList[j].x,2)+pow(snowList[i].y-snowList[j].y,2));
//		for(double k=0;k<distance;k+=size*1.4){
//			double slope = atan2(snowList[j].y-snowList[i].y, snowList[j].x-snowList[i].x);
//			if(matrix[i][j])
//				g.drawImage(Main.pic[Main.PIC_LINE_ON],(int)(snowList[i].x+cos(slope)*k-size/2),(int)(snowList[i].y+sin(slope)*k-size/2),null);
//			else
//				g.drawImage(Main.pic[Main.PIC_LINE_OFF],(int)(snowList[i].x+cos(slope)*k-size/2),(int)(snowList[i].y+sin(slope)*k-size/2),null);
//		}


		//凝った線を描く
		for(int k=5;k>=1;k--){
			if(matrix[i][j])
				g.setColor(new Color(255, 255, 255, (int)pow(6-k,2.8)));
			else
				g.setColor(new Color(0, 0, 0, (int)pow(6-k,2.8)));

			g.setStroke(new BasicStroke((float)pow(k,1.8)));
			g.draw(new Line2D.Double(snowList[i].x,snowList[i].y,snowList[j].x,snowList[j].y));
		}
	}
}
