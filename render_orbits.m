function render_orbits(start_file, target_file, ship_file)
    arguments
        start_file string
        target_file string
        ship_file string
    end
    
    start_data = load_track(start_file);
    target_data = load_track(target_file);
    ship_data = load_track(ship_file);

    start_data = filter_track(start_data, 10);
    target_data = filter_track(target_data, 10);
    ship_data = filter_track(ship_data, 5);

    plot_orbits(start_data, target_data, ship_data);
end