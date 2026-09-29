function [filtered_track] = filter_track(track, step_size)
    arguments
        track table
        step_size int32
    end
    filtered_track = track(1:step_size:end, :);
end