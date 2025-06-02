#include <stdio.h>
#include <gtk/gtk.h>

static void print_net (GtkWidget *widget, gpointer   data)
{
    g_print ("New net\n");
}

static void activate (GtkApplication *app,gpointer        user_data)
{
    GtkWidget *window;
    GtkWidget *button;

    window = gtk_application_window_new (app);
    gtk_window_set_title (GTK_WINDOW (window), "Netrunning tool");
    gtk_window_set_default_size (GTK_WINDOW (window), 600, 600);

    button = gtk_button_new_with_label ("Generate new net");
    g_signal_connect (button, "clicked", G_CALLBACK (print_net), NULL);
    gtk_window_set_child (GTK_WINDOW (window), button);

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