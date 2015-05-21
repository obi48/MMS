/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mms;

/**
 *
 * @author Michael Obermüller
 */
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Simple animation of labels related to RMOUG Training Days 2012 using JavaFX.
 * 
 * @author Dustin
 */
public class Test extends Application
{
   /**
    * Generate Path upon which animation will occur.
    * 
    * @return Generated path.
    */
   private Path generateCurvyPath()
   {
      final Path path = new Path();
      path.getElements().add(new MoveTo(70,20));
      path.getElements().add(new CubicCurveTo(430, 0, 430, 120, 250, 120));
      path.getElements().add(new CubicCurveTo(50, 120, 50, 240, 430, 240));
      path.setOpacity(0.0);
      return path;
   }

   /**
    * Generate the path transition.
    * 
    * @param shape Shape to travel along path.
    * @param path Path to be traveled upon.
    * @param duration Duration of single animation.
    * @param delay Delay before beginning first animation.
    * @param orientation Orientation of shape during animation.
    * @return PathTransition.
    */
   private PathTransition generatePathTransition(
      final Shape shape, final Path path,
      final Duration duration, final Duration delay,
      final OrientationType orientation)
   {
      final PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(duration);
      pathTransition.setDelay(delay);
      pathTransition.setPath(path);
      pathTransition.setNode(shape);
      pathTransition.setOrientation(orientation);
      pathTransition.setCycleCount(Timeline.INDEFINITE);
      pathTransition.setAutoReverse(true);
      return pathTransition;
   }

   /**
    * Generate RMOUG text string with appropriate fill, font, and effect.
    * 
    * @return "RMOUG" text string with fill, font, and effect.
    */
   private Text generateRmougText()
   {
      return TextBuilder.create().text("RMOUG").x(20).y(20).fill(Color.DARKGRAY)
                        .font(Font.font(java.awt.Font.SERIF, 75))
                        .effect(new Glow(0.25)).build();
   }

   /**
    * Generate "Training Days 2012" text string with appropriate position, fill,
    * and font.
    * 
    * @return "Training Days 2012" with specified font, fill, and position.
    */
   private Text generateTrainingDaysText()
   {
      return TextBuilder.create().text("Training Days 2012")
                        .x(380).y(240).fill(Color.DARKOLIVEGREEN)
                        .font(Font.font(java.awt.Font.SERIF, 50)).build();
   }

   /**
    * Location String with specifed effect, font, and position.
    * 
    * @return Location String with specified effect, font, and position.
    */
   private Text generateDenverText()
   {
      final Reflection reflection = new Reflection();
      reflection.setFraction(1.0);
      return TextBuilder.create()
                        .text("Denver, Colorado").x(20).y(20)
                        .font(Font.font(java.awt.Font.SANS_SERIF, 25))
                        .effect(reflection)
                        .build();
   }

   /**
    * Apply animation.
    *  
    * @param group Group to which animation is to be applied.
    */
   private void applyAnimation(final Group group)
   {
      final Path path = generateCurvyPath();
      group.getChildren().add(path);
      final Shape rmoug = generateRmougText();
      group.getChildren().add(rmoug);
      final Shape td = generateTrainingDaysText();
      group.getChildren().add(td);
      final Shape denver = generateDenverText();
      group.getChildren().add(denver);
      final PathTransition rmougTransition =
         generatePathTransition(
            rmoug, path, Duration.seconds(8.0), Duration.seconds(0.5),
            OrientationType.NONE);
      final PathTransition tdTransition =
         generatePathTransition(
            td, path, Duration.seconds(5.5), Duration.seconds(0.1),
            OrientationType.NONE);
      final PathTransition denverTransition =
         generatePathTransition(
            denver, path, Duration.seconds(30), Duration.seconds(3),
            OrientationType.ORTHOGONAL_TO_TANGENT);
      final ParallelTransition parallelTransition =
         new ParallelTransition(rmougTransition, tdTransition, denverTransition);
      parallelTransition.play(); 
   }

   /**
    * JavaFX Application starting method.
    * 
    * @param stage Primary stage.
    * @throws Exception Potential JavaFX application exception.
    */
   @Override
   public void start(Stage stage) throws Exception
   {
      final Group rootGroup = new Group();
      final Scene scene = new Scene(rootGroup, 500, 400, Color.GHOSTWHITE);
      stage.setScene(scene);
      stage.setTitle("JavaFX 2 RMOUG Training Days 2012 Animations");
      stage.show();
      applyAnimation(rootGroup);
   }

   /**
    * Main function for running JavaFX animation demo.
    * 
    * @param arguments Command-line arguments; none expected.
    */
   public static void main(final String[] arguments)
   {
      Application.launch(arguments);
   }
}
