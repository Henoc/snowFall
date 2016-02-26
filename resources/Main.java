package snowFall;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

import eduGUI.AlphaPngDraw;

/*
 * 作者　34765
 * ３回目？２回目？だかのぶつ切れLINEが降るやつ、速度速すぎて無理では？
 */

public class Main extends JPanel implements MouseListener,MouseMotionListener {

	static final int w = 640;
	static final int h = 480;
	static final int e_w=480;		//ゲーム画面のサイズ

	static final Font FONT_TITLE = new Font("Serif",Font.PLAIN,24);
	static final Font FONT_RIGHTMES = new Font("Serif",Font.PLAIN,16);
	static final Font FONT_NUMBERS = new Font("Serif",Font.PLAIN,20);

	static int gameMode=0;

	static Main main;
	static ScheduledTask task;

	static BufferedImage[] pic = new BufferedImage[24];
	static final int PIC_BG = 0;
	static final int PIC_SNOW = 1;
	static final int PIC_CRYSTAL = 2;
	static final int PIC_SNOW_ANIM5 = 3;		//3,4,5,6,7を使用
	static final int PIC_LINE_ON = 8;
	static final int PIC_LINE_OFF = 9;
	static final int PIC_TITLE=10;
	static final int PIC_COMBO=11;
	static final int PIC_NUMBERS10=12;			//12~22を使用
	static final int PIC_1UP=23;

	static ClipControl[] wav = new ClipControl[7];
	static final int WAV_GOOD = 0;
	static final int WAV_CHANGE=1;
	static final int WAV_1UP=2;
	static final int WAV_BUTTON=3;
	static final int WAV_OPENING=4;
	static final int WAV_MISS=5;
	static final int WAV_GAMEOVER=6;

	static MIDIControl[] mid = new MIDIControl[1];
	static final int MID_CRYSTAL=0;

	public static void main(String[] args) {

		//画像読み込み操作一式
		try {
			pic[PIC_BG] = 			ImageIO.read(new File("GameStdio/snowfall/bgXmas.jpg"));		//pro.photo.ne.jp
			pic[PIC_SNOW]=			ImageIO.read(new File("GameStdio/snowfall/snowBefore.png"));
			pic[PIC_CRYSTAL]=		ImageIO.read(new File("GameStdio/snowfall/snowAfter.png"));
			pic[PIC_SNOW_ANIM5+0]=	ImageIO.read(new File("GameStdio/snowfall/snowChanges1.png"));
			pic[PIC_SNOW_ANIM5+1]=	ImageIO.read(new File("GameStdio/snowfall/snowChanges2.png"));
			pic[PIC_SNOW_ANIM5+2]=	ImageIO.read(new File("GameStdio/snowfall/snowChanges3.png"));
			pic[PIC_SNOW_ANIM5+3]=	ImageIO.read(new File("GameStdio/snowfall/snowChanges4.png"));
			pic[PIC_SNOW_ANIM5+4]=	ImageIO.read(new File("GameStdio/snowfall/snowChanges5.png"));
//			pic[PIC_LINE_ON]=		ImageIO.read(new File("GameStdio/snowfall/linearElementON.png"));
//			pic[PIC_LINE_OFF]=		ImageIO.read(new File("GameStdio/snowfall/linearElementOFF.png"));
			pic[PIC_TITLE]=			ImageIO.read(new File("GameStdio/snowfall/title.png"));
			pic[PIC_COMBO]=			ImageIO.read(new File("GameStdio/snowfall/combo.png"));
			for(int i=0;i<10;i++)
				pic[PIC_NUMBERS10+i]=	ImageIO.read(new File("GameStdio/snowfall/comboNumbers"+i+".png"));
			pic[PIC_1UP]=			ImageIO.read(new File("GameStdio/snowfall/1UP.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		//効果音読み込み操作一式
		wav[WAV_GOOD]=				new ClipControl(new File("GameStdio/snowfall/good.wav"));			//魔王魂　より
		wav[WAV_CHANGE]=			new ClipControl(new File("GameStdio/snowfall/change.wav"));
		wav[WAV_1UP]=				new ClipControl(new File("GameStdio/snowfall/1UP.wav"));
		wav[WAV_BUTTON]=			new ClipControl(new File("GameStdio/snowfall/button.wav"));
		wav[WAV_OPENING]=			new ClipControl(new File("GameStdio/snowfall/opening.wav"));
		wav[WAV_MISS]=				new ClipControl(new File("GameStdio/snowfall/miss.wav"));
		wav[WAV_GAMEOVER]=			new ClipControl(new File("GameStdio/snowfall/gameover.wav"));

		//MIDI読み込み操作一式
//		mid[MID_CRYSTAL]=			new MIDIControl("GameStdio/snowfall/crystal.mid");

		//BGMはしっくりこないのでコメントアウト

		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setLocationRelativeTo(null);
		main = new Main();
		frame.add(main);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		//タイマーの設定
		task = new ScheduledTask(main);
		Timer timer = new Timer("GameProcess");
		timer.scheduleAtFixedRate(task,0,16);			//16msごとに更新（60fps）


		//オープニング効果音
		wav[WAV_OPENING].start();
	}

	//ウィンドウ作成時にバッファを取る
	//他のオブジェクトはScheduledTaskで生成して持っておく方がいいと思われる
	BufferedImage image;
	public Main(){
		this.setPreferredSize(new Dimension(w,h));
		image = new BufferedImage(w, h,BufferedImage.TYPE_INT_ARGB);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	//すべてのオブジェクトを背面から順にBGに描画（ここにはpaintMachineのみ書く）
	public void paintComponent(Graphics fg){
		super.paintComponent(fg);
		Graphics2D g = (Graphics2D)image.getGraphics();
		Main.paintMachine(g);
		RightSide.paintMachine(g);
		//処理落ちが激しいと描画前にListのsize変更することによるOutOfBoundsのエラーが出るが、どうせただの描画処理なのでその時は無視して続ける
		try{
			for(int i=ScheduledTask.snowMatrixList.size()-1;i>=0;i--)			//古いSnowMatrixを前面に描画する
				ScheduledTask.snowMatrixList.get(i).paintMachine(g);
			for(Combo e : Combo.comboList)
				e.paintMachine(g);
			GameOver.paintMachine(g);
			for(GameOver btn : ScheduledTask.gameOverList)
				btn.paintButtonMachine(g);
		}catch(Exception e){

		}

		fg.drawImage(image,0,0,null);
	}

	//背景の描画処理
	static private void paintMachine(Graphics2D g) {
		g.drawImage(pic[PIC_BG],0,0,w,h,null);
		g.setColor(new Color(0,0,0,140));
		g.fillRect(0, 0, w, h);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ScheduledTask.msLeft=true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {				//mouseMovedにより常にカーソル座標を取る
		ScheduledTask.msx=e.getX();
		ScheduledTask.msy=e.getY();
	}

}
