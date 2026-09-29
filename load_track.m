function [track_data] = load_track(filename)
    arguments
        filename string
    end
    track_data = readtable(filename);
    track_data.Properties.VariableNames = {'X', 'Y', 'Z'};
end