#include <stdio.h>
#include <gtk/gtk.h>


const int WINDOW_WIDTH = 800;
const int WINDOW_HEIGHT = 600;

static void print_net (GtkWidget *widget, gpointer   data)
{
    g_print ("New net\n");
}

static void activate (GtkApplication *app,gpointer        user_data)
{
    GtkWidget *window;
    GtkWidget *new_net_button;
    GtkWidget *new_profile_button;
    GtkWidget *grid;

    /* set the main window */
    window = gtk_application_window_new (app);
    gtk_window_set_title (GTK_WINDOW (window), "Netrunning tool");
    gtk_window_set_default_size (GTK_WINDOW (window), WINDOW_WIDTH, WINDOW_HEIGHT);

    /* grid component*/
    grid = gtk_grid_new ();
    gtk_window_set_child (GTK_WINDOW (window), grid);

    new_net_button = gtk_button_new_with_label ("Generate new net");
    g_signal_connect (new_net_button, "clicked", G_CALLBACK (print_net), NULL);

    /* positioning the button on the grid*/
    gtk_grid_attach (GTK_GRID (grid), new_net_button, 0, 0, 1, 1);

    new_profile_button = gtk_button_new_with_label ("Generate new profile");
    g_signal_connect (new_profile_button, "clicked", G_CALLBACK (print_net), NULL);
    gtk_grid_attach (GTK_GRID (grid), new_profile_button, 1, 0, 1, 1);

    gtk_window_present (GTK_WINDOW (window));
}

int main (int argc, char **argv)
{
    GtkApplication *app;
    int status;

    app = gtk_application_new ("com.tetokeguii.netrunning", G_APPLICATION_DEFAULT_FLAGS);
    g_signal_connect (app, "activate", G_CALLBACK (activate), NULL);
    status = g_application_run (G_APPLICATION (app), argc, argv);
    g_object_unref (app);

    return status;
}