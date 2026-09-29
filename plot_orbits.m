function plot_orbits(start, target, ship)
    arguments
        start table
        target table
        ship table
    end
    figure("Name", "Transfer Orbit");
    view(3);
    grid on;
    hold on;
    axis equal;
    plot3(0, 0, 0, 'o', "MarkerFaceColor", 'y', "MarkerEdgeColor", 'y', "MarkerSize", 25);
    plot3(start, 'X', 'Y', 'Z', "MarkerEdgeColor", 'b', "MarkerFaceColor", 'b', "MarkerSize", 4, "Marker", "o");
    plot3(target, 'X', 'Y', 'Z', "MarkerEdgeColor", 'r', "MarkerFaceColor", 'r', "MarkerSize", 4, "Marker", "o");
    plot3(ship, 'X', 'Y', 'Z', "MarkerEdgeColor", 'g', "MarkerFaceColor", 'g', "MarkerSize", 2, "Marker", "o");
end