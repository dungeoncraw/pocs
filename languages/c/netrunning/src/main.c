#include <stdio.h>
#include <gtk/gtk.h>


const int WINDOW_WIDTH = 800;
const int WINDOW_HEIGHT = 600;

static void quit_cb (GtkWindow *window)
{
    gtk_window_close (window);
}

static void print_net (GtkWidget *widget, gpointer   data)
{
    g_print ("New net\n");
}

static void print_deck (GtkWidget *widget, gpointer   data)
{
    g_print ("New deck\n");
}

static void activate (GtkApplication *app,gpointer        user_data)
{
    GtkBuilder *builder = gtk_builder_new ();
    gtk_builder_add_from_file (builder, "src/builder.ui", NULL);
    // get reference from builder.ui for the tree object
    GObject *window = gtk_builder_get_object (builder, "window");
    gtk_window_set_application(GTK_WINDOW(window), app);

    // get the reference for button and add signal for logic
    GObject *button = gtk_builder_get_object (builder, "generateNet");
    g_signal_connect(button, "clicked", G_CALLBACK(print_net), NULL);

    button = gtk_builder_get_object (builder, "generateDeck");
    g_signal_connect(button, "clicked", G_CALLBACK(print_deck), NULL);

    button = gtk_builder_get_object (builder, "quit");
    g_signal_connect_swapped(button, "clicked", G_CALLBACK(quit_cb), window);

    gtk_widget_set_visible(GTK_WIDGET(window), TRUE);

    g_object_unref(builder);
}

int main (int argc, char **argv)
{
    #ifdef GTK_SRCDIR
        g_chdir (GTK_SRCDIR);
    #endif

    GtkApplication *app = gtk_application_new ("com.tetokeguii.netrunning", G_APPLICATION_DEFAULT_FLAGS);
    g_signal_connect (app, "activate", G_CALLBACK (activate), NULL);
    const int status = g_application_run (G_APPLICATION (app), argc, argv);
    g_object_unref (app);
    return status;
}